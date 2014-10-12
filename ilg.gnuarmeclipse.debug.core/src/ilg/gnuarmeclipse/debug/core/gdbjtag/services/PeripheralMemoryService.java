/*******************************************************************************
 * Copyright (c) 2014 Liviu Ionescu.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Liviu Ionescu - initial version
 *******************************************************************************/

package ilg.gnuarmeclipse.debug.core.gdbjtag.services;

import ilg.gnuarmeclipse.debug.core.Activator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import org.eclipse.cdt.core.IAddress;
import org.eclipse.cdt.dsf.concurrent.DataRequestMonitor;
import org.eclipse.cdt.dsf.concurrent.IDsfStatusConstants;
import org.eclipse.cdt.dsf.concurrent.ImmediateDataRequestMonitor;
import org.eclipse.cdt.dsf.concurrent.ImmediateExecutor;
import org.eclipse.cdt.dsf.concurrent.ImmediateRequestMonitor;
import org.eclipse.cdt.dsf.concurrent.RequestMonitor;
import org.eclipse.cdt.dsf.concurrent.Sequence;
import org.eclipse.cdt.dsf.datamodel.DMContexts;
import org.eclipse.cdt.dsf.debug.service.IExpressions;
import org.eclipse.cdt.dsf.debug.service.IExpressions.IExpressionDMContext;
import org.eclipse.cdt.dsf.debug.service.IRunControl.IContainerDMContext;
import org.eclipse.cdt.dsf.debug.service.IRunControl.IExitedDMEvent;
import org.eclipse.cdt.dsf.gdb.service.command.IGDBControl;
import org.eclipse.cdt.dsf.mi.service.MIMemory;
import org.eclipse.cdt.dsf.mi.service.command.CommandFactory;
import org.eclipse.cdt.dsf.mi.service.command.output.CLIShowEndianInfo;
import org.eclipse.cdt.dsf.mi.service.command.output.MIDataEvaluateExpressionInfo;
import org.eclipse.cdt.dsf.mi.service.command.output.MIGDBShowLanguageInfo;
import org.eclipse.cdt.dsf.mi.service.command.output.MIInfo;
import org.eclipse.cdt.dsf.service.DsfServiceEventHandler;
import org.eclipse.cdt.dsf.service.DsfSession;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.model.MemoryByte;
import org.osgi.framework.BundleContext;

/**
 * More or less a duplicate of GDBMemory from CDT 8.3, but without any cache.
 * 
 */
public class PeripheralMemoryService extends MIMemory implements
		IPeripheralMemoryService {

	// ------------------------------------------------------------------------

	private IGDBControl fCommandControl;

	/**
	 * Cache of the address sizes for each memory context.
	 */
	private Map<IMemoryDMContext, Integer> fAddressSizes = new HashMap<IMemoryDMContext, Integer>();

	/**
	 * We assume the endianness is the same for all processes because GDB
	 * supports only one target.
	 */
	private Boolean fIsBigEndian;

	// ------------------------------------------------------------------------

	public PeripheralMemoryService(DsfSession session) {
		super(session);
	}

	@Override
	public void initialize(final RequestMonitor rm) {

		System.out.println("PeripheralMemoryService.initialize()");
		super.initialize(new ImmediateRequestMonitor(rm) {
			@Override
			protected void handleSuccess() {
				doInitialize(rm);
			}
		});
	}

	private void doInitialize(final RequestMonitor rm) {

		fCommandControl = getServicesTracker().getService(IGDBControl.class);

		// Register this service to DSF.
		// For completeness, use both the interface and the class name.
		// Used in PeripheralMemoryBlockExtension by interface name.
		register(new String[] { IPeripheralMemoryService.class.getName(),
				PeripheralMemoryService.class.getName(), },
				new Hashtable<String, String>());

		System.out.println("PeripheralMemoryService registered " + this);

		rm.done();
	}

	@Override
	public void shutdown(RequestMonitor rm) {

		System.out.println("PeripheralMemoryService.shutdown()");

		// Remove this service from DSF.
		unregister();

		fAddressSizes.clear();

		super.shutdown(rm);
	}

	// ------------------------------------------------------------------------

	@Override
	protected BundleContext getBundleContext() {
		return Activator.getInstance().getBundle().getBundleContext();
	}

	@Override
	public void initializeMemoryData(final IMemoryDMContext memContext,
			RequestMonitor rm) {

		ImmediateExecutor.getInstance().execute(
				new Sequence(getExecutor(), rm) {

					private String originalLanguage = MIGDBShowLanguageInfo.AUTO;
					private boolean abortLanguageSteps = false;

					// Need a global here as getSteps() can be called more than
					// once.
					private Step[] steps = null;

					private void determineSteps() {
						ArrayList<Step> stepsList = new ArrayList<Step>();

						if (fAddressSizes.get(memContext) == null) {
							stepsList.add(new Step() {
								// store original language
								@Override
								public void execute(
										final RequestMonitor requestMonitor) {
									fCommandControl
											.queueCommand(
													fCommandControl
															.getCommandFactory()
															.createMIGDBShowLanguage(
																	memContext),
													new ImmediateDataRequestMonitor<MIGDBShowLanguageInfo>(
															requestMonitor) {
														@Override
														protected void handleCompleted() {
															if (isSuccess()) {
																originalLanguage = getData()
																		.getLanguage();
															} else {
																abortLanguageSteps = true;
															}
															requestMonitor
																	.done();
														}
													});
								}
							});
							stepsList.add(new Step() {
								// switch to c language
								@Override
								public void execute(
										final RequestMonitor requestMonitor) {
									if (abortLanguageSteps) {
										requestMonitor.done();
										return;
									}

									fCommandControl
											.queueCommand(
													fCommandControl
															.getCommandFactory()
															.createMIGDBSetLanguage(
																	memContext,
																	MIGDBShowLanguageInfo.C),
													new ImmediateDataRequestMonitor<MIInfo>(
															requestMonitor) {
														@Override
														protected void handleCompleted() {
															if (!isSuccess()) {
																abortLanguageSteps = true;
															}
															// Accept failure
															requestMonitor
																	.done();
														}
													});
								}
							});

							stepsList.add(new Step() {
								// read address size
								@Override
								public void execute(
										final RequestMonitor requestMonitor) {
									// Run this step even if the language
									// commands where aborted, but accept
									// failures.
									readAddressSize(
											memContext,
											new ImmediateDataRequestMonitor<Integer>(
													requestMonitor) {
												@Override
												protected void handleCompleted() {
													if (isSuccess()) {
														fAddressSizes.put(
																memContext,
																getData());
													}
													// Accept failure
													requestMonitor.done();
												}
											});
								}
							});

							stepsList.add(new Step() {
								// restore original language
								@Override
								public void execute(
										final RequestMonitor requestMonitor) {
									if (abortLanguageSteps) {
										requestMonitor.done();
										return;
									}

									fCommandControl
											.queueCommand(
													fCommandControl
															.getCommandFactory()
															.createMIGDBSetLanguage(
																	memContext,
																	originalLanguage),
													new ImmediateDataRequestMonitor<MIInfo>(
															requestMonitor) {
														@Override
														protected void handleCompleted() {
															if (!isSuccess()) {
																// If we are
																// unable to set
																// the original
																// language back
																// things could
																// be bad.
																// Let's try
																// setting it to
																// "auto" as a
																// fall back.
																// Log the
																// situation as
																// info.
																Activator
																		.log(getStatus());

																fCommandControl
																		.queueCommand(
																				fCommandControl
																						.getCommandFactory()
																						.createMIGDBSetLanguage(
																								memContext,
																								MIGDBShowLanguageInfo.AUTO),
																				new ImmediateDataRequestMonitor<MIInfo>(
																						requestMonitor) {
																					@Override
																					protected void handleCompleted() {
																						if (!isSuccess()) {
																							// This
																							// error
																							// could
																							// be
																							// bad
																							// because
																							// we've
																							// changed
																							// the
																							// language
																							// to
																							// C
																							// but
																							// are
																							// unable
																							// to
																							// switch
																							// it
																							// back.
																							// Log
																							// the
																							// error.
																							// If
																							// the
																							// language
																							// happens
																							// to
																							// be
																							// C
																							// anyway,
																							// everything
																							// will
																							// continue
																							// to
																							// work,
																							// which
																							// is
																							// why
																							// we
																							// don't
																							// abort
																							// the
																							// sequence
																							// (which
																							// would
																							// cause
																							// the
																							// entire
																							// session
																							// to
																							// fail).
																							Activator
																									.log(getStatus());
																						}
																						// Accept
																						// failure
																						requestMonitor
																								.done();
																					}
																				});
															} else {
																requestMonitor
																		.done();
															}
														}
													});
								}
							});

						}

						if (fIsBigEndian == null) {
							stepsList.add(new Step() {
								// read endianness
								@Override
								public void execute(
										final RequestMonitor requestMonitor) {
									readEndianness(
											memContext,
											new ImmediateDataRequestMonitor<Boolean>(
													requestMonitor) {
												@Override
												protected void handleCompleted() {
													if (isSuccess()) {
														fIsBigEndian = getData();
													}
													// Accept failure
													requestMonitor.done();
												}
											});
								}
							});
						}

						steps = stepsList.toArray(new Step[stepsList.size()]);
					}

					@Override
					public Step[] getSteps() {
						if (steps == null) {
							determineSteps();
						}

						return steps;
					}
				});
	}

	@DsfServiceEventHandler
	public void eventDispatched(IExitedDMEvent event) {
		if (event.getDMContext() instanceof IContainerDMContext) {
			IMemoryDMContext context = DMContexts.getAncestorOfType(
					event.getDMContext(), IMemoryDMContext.class);
			if (context != null) {
				fAddressSizes.remove(context);
			}
		}
	}

	@Override
	public int getAddressSize(IMemoryDMContext context) {
		Integer addressSize = fAddressSizes.get(context);
		return (addressSize != null) ? addressSize.intValue() : 8;
	}

	@Override
	public boolean isBigEndian(IMemoryDMContext context) {
		assert fIsBigEndian != null;
		if (fIsBigEndian == null) {
			Activator.log(new Status(IStatus.ERROR, Activator.PLUGIN_ID,
					"Endianness was never initialized!")); //$NON-NLS-1$
			return false;
		}
		return fIsBigEndian;
	}

	protected void readAddressSize(IMemoryDMContext memContext,
			final DataRequestMonitor<Integer> drm) {
		IExpressions exprService = getServicesTracker().getService(
				IExpressions.class);
		IExpressionDMContext exprContext = exprService.createExpression(
				memContext, "sizeof (void*)"); //$NON-NLS-1$
		CommandFactory commandFactory = fCommandControl.getCommandFactory();
		fCommandControl.queueCommand(commandFactory
				.createMIDataEvaluateExpression(exprContext),
				new DataRequestMonitor<MIDataEvaluateExpressionInfo>(
						ImmediateExecutor.getInstance(), drm) {
					@Override
					protected void handleSuccess() {
						try {
							drm.setData(Integer.decode(getData().getValue()));
						} catch (NumberFormatException e) {
							drm.setStatus(new Status(
									IStatus.ERROR,
									Activator.PLUGIN_ID,
									String.format(
											"Invalid address size: %s", getData().getValue()))); //$NON-NLS-1$
						}
						drm.done();
					}
				});
	}

	protected void readEndianness(IMemoryDMContext memContext,
			final DataRequestMonitor<Boolean> drm) {
		CommandFactory commandFactory = fCommandControl.getCommandFactory();
		fCommandControl.queueCommand(
				commandFactory.createCLIShowEndian(memContext),
				new DataRequestMonitor<CLIShowEndianInfo>(ImmediateExecutor
						.getInstance(), drm) {
					@Override
					protected void handleSuccess() {
						drm.setData(Boolean.valueOf(getData().isBigEndian()));
						drm.done();
					}
				});
	}

	// ------------------------------------------------------------------------

	@Override
	public void getMemory(IMemoryDMContext memoryDMC, IAddress address,
			long offset, int word_size, int word_count,
			DataRequestMonitor<MemoryByte[]> drm) {

		if (memoryDMC == null) {
			drm.setStatus(new Status(IStatus.ERROR, Activator.PLUGIN_ID,
					INTERNAL_ERROR, "Unknown context type", null)); //$NON-NLS-1$);
			drm.done();
			return;
		}

		if (word_size < 1) {
			drm.setStatus(new Status(IStatus.ERROR, Activator.PLUGIN_ID,
					NOT_SUPPORTED, "Word size not supported (< 1)", null)); //$NON-NLS-1$
			drm.done();
			return;
		}

		if (word_count < 0) {
			drm.setStatus(new Status(IStatus.ERROR, Activator.PLUGIN_ID,
					IDsfStatusConstants.INTERNAL_ERROR,
					"Invalid word count (< 0)", null)); //$NON-NLS-1$
			drm.done();
			return;
		}

		flushCache(memoryDMC);
		readMemoryBlock(memoryDMC, address, offset, 1, word_count * word_size,
				drm);
	}

	@Override
	public void setMemory(IMemoryDMContext memoryDMC, IAddress address,
			long offset, int word_size, int word_count, byte[] buffer,
			RequestMonitor rm) {

		if (memoryDMC == null) {
			rm.setStatus(new Status(IStatus.ERROR, Activator.PLUGIN_ID,
					INTERNAL_ERROR, "Unknown context type", null)); //$NON-NLS-1$);
			rm.done();
			return;
		}

		if (word_size < 1) {
			rm.setStatus(new Status(IStatus.ERROR, Activator.PLUGIN_ID,
					NOT_SUPPORTED, "Word size not supported (< 1)", null)); //$NON-NLS-1$
			rm.done();
			return;
		}

		if (word_count < 0) {
			rm.setStatus(new Status(IStatus.ERROR, Activator.PLUGIN_ID,
					IDsfStatusConstants.INTERNAL_ERROR,
					"Invalid word count (< 0)", null)); //$NON-NLS-1$
			rm.done();
			return;
		}

		if (buffer.length < word_count * word_size) {
			rm.setStatus(new Status(IStatus.ERROR, Activator.PLUGIN_ID,
					IDsfStatusConstants.INTERNAL_ERROR,
					"Buffer too short", null)); //$NON-NLS-1$
			rm.done();
			return;
		}

		// System.out.print("Write 0x");
		// for (int i=0; i < buffer.length; ++i){
		// System.out.print(String.format(" %02X", buffer[i]));
		// }
		// System.out.println(" @ 0x"+address.toString(16)+"+0x"+String.format("%X",
		// offset));

		flushCache(memoryDMC);
		writeMemoryBlock(memoryDMC, address, offset, 1, word_count * word_size,
				buffer, rm);

		// TODO: maybe notify MemoryChangedEvent
	}

	// ------------------------------------------------------------------------
}
