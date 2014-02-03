#if defined(DEBUG)

  /*
   * Show the program parameters (passed via semihosting).
   * Output is via the semihosting output channel.
   */
  printf("main(argc=%d, argv=[", argc);
  for (int i = 0; i < argc; ++i)
    {
      if (i != 0)
        {
          printf(", ");
        }
      printf("'%s'", argv[i]);
    }
  printf("])\n");

  /*
   * Send a greeting.
   */
  printf("Hello ARM World!\n");

#endif

