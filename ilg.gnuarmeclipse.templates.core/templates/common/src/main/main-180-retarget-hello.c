  // By customising __initialize_args() it is possible to pass arguments,
  // for example when running tests with semihosting you can pass various
  // options to the test.
  // trace_dump_args(argc, argv);

  // Send a greeting to the trace device (skipped on Release).
  trace_puts("Hello ARM World!");

  // The standard output should be forwarded to the trace device.
  puts("Standard output message.");

  // The standard error should be forwarded to the trace device.
  fprintf(stderr, "Standard error message.\n");

