/*
 * File:    uif.c
 * Purpose: Provide an interactive user interface
 *              
 * Notes:   The commands, set/show parameters, and prompt are configured 
 *          at the project level
 */

#include "common.h"
#include "uif.h"
/********************************************************************/
/*
 * Global messages -- constant strings
 */
const char HELPMSG[] =
    "Enter 'help' for help.\n";

const char INVARG[] =
    "Error: Invalid argument: %s\n";

const char INVALUE[] = 
    "Error: Invalid value: %s\n";

/*
 * Strings used by this file only
 */
static const char INVCMD[] =
    "Error: No such command: %s\n";

static const char HELPFORMAT[] = 
    "%8s  %-25s %s %s\n";

static const char SYNTAX[] = 
    "Error: Invalid syntax for: %s\n";

static const char INVOPT[] = 
    "Error:  Invalid set/show option: %s\n";

static const char OPTFMT[] = 
    "%12s: ";

static char cmdline1 [UIF_MAX_LINE];
static char cmdline2 [UIF_MAX_LINE];

/********************************************************************/
char *
get_line (char *line)
{
    int pos;
    int ch;

    pos = 0;
    ch = (int)in_char();
    while ( (ch != 0x0D /* CR */) &&
            (ch != 0x0A /* LF/NL */) &&
            (pos < UIF_MAX_LINE))
    {
        switch (ch)
        {
            case 0x08:      /* Backspace */
            case 0x7F:      /* Delete */
                if (pos > 0)
                {
                    pos -= 1;
                    out_char(0x08);    /* backspace */
                    out_char(' ');
                    out_char(0x08);    /* backspace */
                }
                break;
            default:
                if ((pos+1) < UIF_MAX_LINE)
                {
                    if ((ch > 0x1f) && (ch < 0x80))
                    {
                        line[pos++] = (char)ch;
                        out_char((char)ch);
                    }
                }
                break;
        }
        ch = (int)in_char();
    }
    line[pos] = '\0';
    out_char(0x0D);    /* CR */
    out_char(0x0A);    /* LF */

    return line;
}

/********************************************************************/
int
make_argv (char *cmdline, char *argv[])
{
    int argc, i, in_text;

    /* 
     * Break cmdline into strings and argv
     * It is permissible for argv to be NULL, in which case
     * the purpose of this routine becomes to count args
     */
    argc = 0;
    i = 0;
    in_text = FALSE;
    while (cmdline[i] != '\0')  /* getline() must place 0x00 on end */
    {
        if (((cmdline[i] == ' ')   ||
             (cmdline[i] == '\t')) )
        {
            if (in_text)
            {
                /* end of command line argument */
                cmdline[i] = '\0';
                in_text = FALSE;
            }
            else
            {
                /* still looking for next argument */
                
            }
        }
        else
        {
            /* got non-whitespace character */
            if (in_text)
            {
            }
            else
            {
                /* start of an argument */
                in_text = TRUE;
                if (argc < UIF_MAX_ARGS)
                {
                    if (argv != NULL)
                        argv[argc] = &cmdline[i];
                    argc++;
                }
                else
                    /*return argc;*/
                    break;
            }

        }
        i++;    /* proceed to next character */
    }
    if (argv != NULL)
        argv[argc] = NULL;
    return argc;
}

/********************************************************************/
void
run_cmd (void)
{
    /*
     * Global array of pointers to emulate C argc,argv interface
     */
    int argc;
    char *argv[UIF_MAX_ARGS + 1];   /* one extra for null terminator */

    get_line(cmdline1);

    if (!(argc = make_argv(cmdline1,argv)))
    {
        /* no command entered, just a blank line */
        strcpy(cmdline1,cmdline2);
        argc = make_argv(cmdline1,argv);
    }
    cmdline2[0] = '\0';

    if (argc)
    {
        int i;
        for (i = 0; i < UIF_NUM_CMD; i++)
        {
            if (strcasecmp(UIF_CMDTAB[i].cmd,argv[0]) == 0)
            {
                if (((argc-1) >= UIF_CMDTAB[i].min_args) &&
                    ((argc-1) <= UIF_CMDTAB[i].max_args))
                {
                    if (UIF_CMDTAB[i].flags & UIF_CMD_FLAG_REPEAT)
                    {
                        strcpy(cmdline2,argv[0]);
                    }
                    UIF_CMDTAB[i].func(argc,argv);
                    return;
                }
                else
                {
                    printf(SYNTAX,argv[0]);
                    return;
                }
            }
        }
        printf(INVCMD,argv[0]);
        printf(HELPMSG);
    }
}
/********************************************************************/
uint32
get_value (char *s, int *success, int base)
{
    uint32 value;
    char *p;

    value = strtoul(s,&p,base);
    if ((value == 0) && (p == s))
    {
        *success = FALSE;
        return 0;
    }
    else
    {
        *success = TRUE;
        return value;
    }
}
/********************************************************************/
void
uif_cmd_help (int argc, char **argv)
{
    int index;
    
    (void)argc;
    (void)argv;
    
    printf("\n");
    for (index = 0; index < UIF_NUM_CMD; index++)
    {
        printf(HELPFORMAT,
            UIF_CMDTAB[index].cmd,
            UIF_CMDTAB[index].description,
            UIF_CMDTAB[index].cmd,
            UIF_CMDTAB[index].syntax);
    }
    printf("\n");
}
/********************************************************************/
void
uif_cmd_set (int argc, char **argv)
{
    int index;

    printf("\n");
    if (argc == 1)
    {
        printf("Valid 'set' options:\n");
        for (index = 0; index < UIF_NUM_SETCMD; ++index)
        {
            printf(OPTFMT,UIF_SETCMDTAB[index].option);
            printf("%s\n",UIF_SETCMDTAB[index].syntax);
        }
        printf("\n");
        return;
    }

    if (argc != 3)
    {
        printf("Error: Invalid argument list\n");
        return;
    }

    for (index = 0; index < UIF_NUM_SETCMD; index++)
    {
        if (strcasecmp(UIF_SETCMDTAB[index].option,argv[1]) == 0)
        {
            if (((argc-1-1) >= UIF_SETCMDTAB[index].min_args) &&
                ((argc-1-1) <= UIF_SETCMDTAB[index].max_args))
            {
                UIF_SETCMDTAB[index].func(argc,argv);
                return;
            }
            else
            {
                printf(INVARG,argv[1]);
                return;
            }
        }
    }
    printf(INVOPT,argv[1]);
}

/********************************************************************/
void
uif_cmd_show (int argc, char **argv)
{
    int index;

    printf("\n");
    if (argc == 1)
    {
        /*
         * Show all Option settings
         */
        argc = 2;
        argv[2] = NULL;
        for (index = 0; index < UIF_NUM_SETCMD; index++)
        {
            printf(OPTFMT,UIF_SETCMDTAB[index].option);
            UIF_SETCMDTAB[index].func(argc,argv);
            printf("\n");
        }
        printf("\n");
        return;
    }

    for (index = 0; index < UIF_NUM_SETCMD; index++)
    {
        if (strcasecmp(UIF_SETCMDTAB[index].option,argv[1]) == 0)
        {
            if (((argc-1-1) >= UIF_SETCMDTAB[index].min_args) &&
                ((argc-1-1) <= UIF_SETCMDTAB[index].max_args))
            {
                printf(OPTFMT,UIF_SETCMDTAB[index].option);
                UIF_SETCMDTAB[index].func(argc,argv);
                printf("\n\n");
                return;
            }
            else
            {
                printf(INVARG,argv[1]);
                return;
            }
        }
    }
    printf(INVOPT,argv[1]);
}

/********************************************************************/
