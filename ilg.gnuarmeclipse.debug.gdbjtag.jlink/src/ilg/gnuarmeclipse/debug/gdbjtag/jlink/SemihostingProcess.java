/*******************************************************************************
 * Copyright (c) 2013 Liviu Ionescu.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Liviu Ionescu - initial version
 *******************************************************************************/

package ilg.gnuarmeclipse.debug.gdbjtag.jlink;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;

/**
 * Synthetic process (does not have a system process), used to get the
 * semihosting output from the J-Link GDB server.
 */
public class SemihostingProcess extends Process implements Runnable {

	boolean fRunning;
	Thread fThread = null;

	Socket fSocket;

	/**
	 * Size of buffer for processing data received from remote endpoint.
	 */
	protected static final int BUFFER_SIZE = 2048;

	/**
	 * Holds raw bytes received from the remote endpoint, prior to any TELNET
	 * protocol processing.
	 */
	protected byte[] fRawBytes = new byte[BUFFER_SIZE];

	/**
	 * This field holds a reference to an {@link InputStream} object used to
	 * receive data from the remote endpoint.
	 */
	protected InputStream fInputStream;
	protected OutputStream fOutputStream;
	protected InputStream fErrorStream;

	// fake input stream, never has data to read
	protected class NullInputStream extends InputStream {

		private boolean fIsOpened = true;
		private Thread fThread = null;

		@Override
		public int read() throws IOException {

			if (!fIsOpened)
				return -1;

			try {
				fThread = Thread.currentThread();
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				if (Activator.getInstance().isDebugging()) {
					System.out.println("NullInputStream.read() interrupted");
				}
			}
			return 0;
		}

		public int available() {
			return 0;
		}

		public void close() throws IOException {

			if (Activator.getInstance().isDebugging()) {
				System.out.println("NullInputStream.close() " + Thread.currentThread());
			}

			if (fIsOpened) {
				super.close();
				fIsOpened = false;
				if (fThread != null) {
					if (Activator.getInstance().isDebugging()) {
						System.out
								.println("NullInputStream.close() interrupt " + Thread.currentThread() + " " + fThread);
					}
					fThread.interrupt();
				}
			}
		}
	}

	// Fake output stream, ignores all writes.
	protected class NullOutputStream extends OutputStream {

		@Override
		public void write(int arg) throws IOException {
		}

	}

	protected PipedInputStream fPipeIn;
	protected PipedOutputStream fPipeOut;

	private String fHost;
	private int fPort;

	public SemihostingProcess(String host, int port) {

		if (Activator.getInstance().isDebugging()) {
			System.out.println("SemihostingProcess(" + host + "," + port + ") " + this);
		}

		fHost = host;
		fPort = port;

		fRunning = false;

		fInputStream = new NullInputStream();
		fErrorStream = new NullInputStream();
		fOutputStream = new NullOutputStream();

		fPipeOut = new PipedOutputStream();

		try {
			fPipeIn = new PipedInputStream(fPipeOut);
		} catch (IOException e) {
			Activator.log(e);
		}
	}

	@Override
	public void destroy() {

		if (Activator.getInstance().isDebugging()) {
			System.out.println("SemihostingProcess.destroy() " + Thread.currentThread() + " " + fThread);
		}

		if (fRunning) {

			if (fThread != null && fThread != Thread.currentThread()) {
				fThread.interrupt();
				if (Activator.getInstance().isDebugging()) {
					System.out.println("SemihostingProcess.destroy() after interrupt");
				}
			}

			try {
				if (fSocket != null && !fSocket.isClosed()) {

					// Do not close(), it is too brutal.
					// socket.close();

					if (fSocket != null && !fSocket.isInputShutdown()) {
						if (Activator.getInstance().isDebugging()) {
							System.out.println("SemihostingProcess.destroy() before shutdownInput");
						}
						fSocket.shutdownInput();
					}
					if (fSocket != null && !fSocket.isOutputShutdown()) {
						if (Activator.getInstance().isDebugging()) {
							System.out.println("SemihostingProcess.destroy() before shutdownOutput");
						}
						fSocket.shutdownOutput();
					}
				}
			} catch (IOException e) {
				// Activator.log(e);
			}
		}

		if (Activator.getInstance().isDebugging()) {
			System.out.println("SemihostingProcess.destroy() return");
		}
	}

	@Override
	public int exitValue() {

		if (fRunning)
			throw new IllegalThreadStateException();

		return 0;
	}

	@Override
	public InputStream getErrorStream() {

		return fErrorStream;
	}

	@Override
	public InputStream getInputStream() {
		return fPipeIn;
	}

	@Override
	public OutputStream getOutputStream() {
		return fOutputStream;
	}

	@Override
	public int waitFor() throws InterruptedException {
		if (Activator.getInstance().isDebugging()) {
			System.out.println("SemihostingProcess.waitFor() " + Thread.currentThread() + " will wait for " + fThread);
		}
		fThread.join();
		if (Activator.getInstance().isDebugging()) {
			System.out.println("SemihostingProcess.waitFor() return " + Thread.currentThread());
		}
		return 0;
	}

	public void run() {

		if (Activator.getInstance().isDebugging()) {
			System.out.println("SemihostingProcess.run() " + Thread.currentThread());
		}

		fRunning = true;

		int nTimeout = 10 * 1000;

		int i = 50;
		// wait max 10sec (50x200ms)
		while (i > 0) {
			InetSocketAddress address = new InetSocketAddress(fHost, fPort);
			fSocket = new Socket();
			try {
				fSocket.connect(address, nTimeout);
				break;
			} catch (IOException e1) {
				--i;
				try {
					Thread.sleep(200);
				} catch (InterruptedException e) {
				}
			}
			if (i == 0) {
				// cannot connect, no way to go further
				if (Activator.getInstance().isDebugging()) {
					System.out.println("cannot connect, no way to go further.");
				}

				return;
			}
		}

		fRunning = true;

		try {

			fInputStream = fSocket.getInputStream();
			fOutputStream = fSocket.getOutputStream();

			fSocket.setKeepAlive(true);

			while (fSocket.isConnected() & fRunning) {

				int nRawBytes;
				try {
					nRawBytes = fInputStream.read(fRawBytes);
				} catch (SocketException e) {
					nRawBytes = -1; // EOS
					// System.out.println(e);
				}

				if (nRawBytes == -1) {
					// End of input on inputStream.

					// Announce to the user that the remote endpoint has closed
					// the connection.

					if (Activator.getInstance().isDebugging()) {
						System.out.println("SemihostingProcess.run() Connection closed by the GDB server.");
					}

					fPipeOut.write("Connection closed by the GDB server.".getBytes());

					break;
				} else {
					if (nRawBytes > 0) {
						fPipeOut.write(fRawBytes, 0, nRawBytes);
					}
				}
			}
		} catch (IOException e) {
			Activator.log(e);
		} finally {
			try {
				fPipeOut.close();
			} catch (IOException e1) {
				;
			}

			try {
				fErrorStream.close();
			} catch (IOException e1) {
				;
			}

			try {
				fSocket.close();
				fSocket = null;
			} catch (IOException e) {
				;
			}

			try {
				fInputStream.close();
			} catch (NullPointerException e) {
				;
			} catch (IOException e) {
				;
			}

			fRunning = false;
		}

		if (Activator.getInstance().isDebugging()) {
			System.out.println("SemihostingProcess.run() completed ");
		}
	}

	public void submit() {
		if (Activator.getInstance().isDebugging()) {
			System.out.println("SemihostingProcess.submit() " + Thread.currentThread());
		}
		fThread = new Thread(this);
		fThread.setName("Semihosting and SWV fake process");
		fThread.setDaemon(true);
		fThread.setPriority(Thread.MIN_PRIORITY);
		fThread.start();
	}

}
