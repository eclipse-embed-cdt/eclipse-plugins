  // Show the program parameters (passed via semihosting).
  // Output is via the semihosting output channel.
  trace_dump_args(argc, argv);

  // Send a greeting to the trace device (skipped on Release).
  trace_puts("Hello ARM World!");

  // Send a message to the standard output.
  puts("Standard output message.");

  // Send a message to the standard error.
  fprintf(stderr, "Standard error message.\n");

