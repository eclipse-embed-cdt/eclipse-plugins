
// ----- main() ---------------------------------------------------------------

void
SystemClock_Config();

#if defined(DEBUG)
void
dump_args (int argc, char* argv[]);
#endif

int
main(int argc, char* argv[])
{
  HAL_Init();
  SystemClock_Config();
