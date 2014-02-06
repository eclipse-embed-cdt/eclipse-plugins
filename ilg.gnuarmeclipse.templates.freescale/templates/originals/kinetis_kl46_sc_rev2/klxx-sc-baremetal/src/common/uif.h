/*
 * File:    uif.h
 * Purpose: Provide an interactive user interface
 *              
 * Notes:   The commands, set/show parameters, and prompt are configured 
 *          at the project level
 */

#ifndef _UIF_H_
#define _UIF_H_

/********************************************************************/

/*
 * Function prototypes
 */
char *
get_line (char *);

uint32
get_value (char *, int *, int);

void
run_cmd (void);

int
make_argv (char *, char **);

void
uif_cmd_help (int, char **);

void
uif_cmd_set (int, char **);

void
uif_cmd_show (int, char **);

/*
 * Maximum command line arguments
 */
#define UIF_MAX_ARGS    10

/*
 * Maximum length of the command line
 */
#define UIF_MAX_LINE    80

/*
 * The command table entry data structure
 */
typedef const struct
{
    char *  cmd;                    /* command name user types, ie. GO  */
    int     min_args;               /* min num of args command accepts  */
    int     max_args;               /* max num of args command accepts  */
    int     flags;                  /* command flags (e.g. repeat)      */
    void    (*func)(int, char **);  /* actual function to call          */
    char *  description;            /* brief description of command     */
    char *  syntax;                 /* syntax of command                */
} UIF_CMD;

/*
 * Prototype and macro for size of the command table
 */
#ifdef KEIL
UIF_CMD UIF_CMDTAB[];
const int UIF_NUM_CMD;
#else
extern UIF_CMD UIF_CMDTAB[];
extern const int UIF_NUM_CMD;
#endif 

#define UIF_CMDTAB_SIZE             (sizeof(UIF_CMDTAB)/sizeof(UIF_CMD))

#define UIF_CMD_FLAG_REPEAT         0x1

/*
 * Macros for User InterFace command table entries
 */
#ifndef UIF_CMD_HELP
#define UIF_CMD_HELP    \
    {"help",0,1,0,uif_cmd_help,"Help","<cmd>"},
#endif

#ifndef UIF_CMD_SET
#define UIF_CMD_SET \
    {"set",0,2,0,uif_cmd_set,"Set Config","<option value>"},
#endif

#ifndef UIF_CMD_SHOW
#define UIF_CMD_SHOW    \
    {"show",0,1,0,uif_cmd_show,"Show Config","<option>"},
#endif

/*
 * Macro to include all standard user interface commands
 */
#define UIF_CMDS_ALL    \
    UIF_CMD_HELP        \
    UIF_CMD_SET         \
    UIF_CMD_SHOW

/*
 * The set/show table entry data structure
 */
typedef const struct
{
    char *  option;
    int     min_args;
    int     max_args;
    void    (*func)(int, char **);
    char *  syntax;
} UIF_SETCMD;

/*
 * Prototype and macro for size of the table
 */
#ifdef KEIL
UIF_SETCMD UIF_SETCMDTAB[];
const int UIF_NUM_SETCMD;
#else
extern UIF_SETCMD UIF_SETCMDTAB[];
extern const int UIF_NUM_SETCMD;
#endif

#define UIF_SETCMDTAB_SIZE      (sizeof(UIF_SETCMDTAB)/sizeof(UIF_SETCMD))

/*
 * Strings defined in uif.c that may be useful to external functions
 */
extern const char HELPMSG[];
extern const char INVARG[];
extern const char INVALUE[];

/********************************************************************/

#endif /* _UIF_H_ */
