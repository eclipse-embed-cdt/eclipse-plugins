
      ++seconds;

#if defined(DEBUG)
      // Count seconds on the debug channel.
      printf ("Second %d\n", seconds);
#endif
    }
  // Infinite loop, never return.
}

#pragma GCC diagnostic pop

#if defined(DEBUG)

void
dump_args (int argc, char* argv[])
{
  printf ("main(argc=%d, argv=[", argc);
  for (int i = 0; i < argc; ++i)
    {
      if (i != 0)
	{
	  printf (", ");
	}
      printf ("\"%s\"", argv[i]);
    }
  printf ("]);\n");
}

#endif

