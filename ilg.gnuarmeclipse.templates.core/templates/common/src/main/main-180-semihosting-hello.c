  // Show the program parameters (passed via semihosting).
  // Output is via the semihosting output channel.
  trace_dump_args(argc, argv);

  // Send a greeting to the standard output (skipped on Release).
  trace_puts("Hello ARM World!");

  // Send a message to the standard output.
  puts("This is a semihosting application.");

