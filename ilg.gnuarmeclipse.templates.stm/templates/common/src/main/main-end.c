}

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

