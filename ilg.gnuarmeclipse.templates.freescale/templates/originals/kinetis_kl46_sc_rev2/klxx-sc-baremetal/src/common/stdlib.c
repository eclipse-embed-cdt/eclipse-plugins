/*
 * File:        stdlib.c
 * Purpose:     Functions normally found in a standard C lib.
 *
 * Notes:       This supports ASCII only!!!
 *
 */

#include "common.h"
#include "stdlib.h"

/****************************************************************/
int
isspace (int ch)
{
    if ((ch == ' ') || (ch == '\t'))    /* \n ??? */
        return TRUE;
    else
        return FALSE;
}

/****************************************************************/
int
isalnum (int ch)
{
    /* ASCII only */
    if (((ch >= '0') && (ch <= '9')) ||
        ((ch >= 'A') && (ch <= 'Z')) ||
        ((ch >= 'a') && (ch <= 'z')))
        return TRUE;
    else
        return FALSE;
}

/****************************************************************/
int
isdigit (int ch)
{
    /* ASCII only */
    if ((ch >= '0') && (ch <= '9'))
        return TRUE;
    else
        return FALSE;
}

/****************************************************************/
int
isupper (int ch)
{
    /* ASCII only */
    if ((ch >= 'A') && (ch <= 'Z'))
        return TRUE;
    else
        return FALSE;
}

/****************************************************************/
int
strcasecmp (const char *s1, const char *s2)
{
    char    c1, c2;
    int     result = 0;

    while (result == 0)
    {
        c1 = *s1++;
        c2 = *s2++;
        if ((c1 >= 'a') && (c1 <= 'z'))
            c1 = (char)(c1 - ' ');
        if ((c2 >= 'a') && (c2 <= 'z'))
            c2 = (char)(c2 - ' ');
        if ((result = (c1 - c2)) != 0)
            break;
        if ((c1 == 0) || (c2 == 0))
            break;
    }
    return result;
}


/****************************************************************/
int
strncasecmp (const char *s1, const char *s2, int n)
{
    char    c1, c2;
    int     k = 0;
    int     result = 0;

    while ( k++ < n )
    {
        c1 = *s1++;
        c2 = *s2++;
        if ((c1 >= 'a') && (c1 <= 'z'))
            c1 = (char)(c1 - ' ');
        if ((c2 >= 'a') && (c2 <= 'z'))
            c2 = (char)(c2 - ' ');
        if ((result = (c1 - c2)) != 0)
            break;
        if ((c1 == 0) || (c2 == 0))
            break;
    }
    return result;
}

/****************************************************************/
uint32
strtoul (char *str, char **ptr, int base)
{
    unsigned long rvalue;
    int c, err, neg;
    char *endp;
    char *startp;

    rvalue = 0;  err = 0;  neg = 0;

    /* Check for invalid arguments */
    if ((str == NULL) || (base < 0) || (base == 1) || (base > 36))
    {
        if (ptr != NULL)
        {
            *ptr = str;
        }
        return 0;
    }

    /* Skip leading white spaces */
    for (startp = str; isspace(*startp); ++startp)
        ;

    /* Check for notations */
    switch (startp[0])
    {
        case '0':
            if ((startp[1] == 'x') || (startp[1] == 'X'))
            {
                if ((base == 0) || (base == 16))
                {
                    base = 16;
                    startp = &startp[2];
                }
            }
            break;
        case '-':
            neg = 1;
            startp = &startp[1];
            break;
        default:
            break;
    }

    if (base == 0)
        base = 10;

    /* Check for invalid chars in str */
    for ( endp = startp; (c = *endp) != '\0'; ++endp)
    {
        /* Check for 0..9,Aa-Zz */
        if (!isalnum(c))
        {
            err = 1;
            break;
        }

        /* Convert char to num in 0..36 */
        if (isdigit(c))
        {
            c = c - '0';
        }
        else
        {
            if (isupper(c))
            {
                c = c - 'A' + 10;
            }
            else
            {
                c = c - 'a' + 10;
            }
        }

        /* check c against base */
        if (c >= base)
        {
            err = 1;
            break;
        }

        if (neg)
        {
            rvalue = (rvalue * base) - c;
        }
        else
        {
            rvalue = (rvalue * base) + c;
        }
    }

    /* Upon exit, endp points to the character at which valid info */
    /* STOPS.  No chars including and beyond endp are used.        */

    if (ptr != NULL)
        *ptr = endp;

    if (err)
    {
        if (ptr != NULL)
            *ptr = str;
        
        return 0;
    }
    else
    {
        return rvalue;
    }
}

/****************************************************************/
int
strlen (const char *str)
{
    char *s = (char *)str;
    int len = 0;

    if (s == NULL)
        return 0;

    while (*s++ != '\0')
        ++len;

    return len;
}

/****************************************************************/
char *
strcat (char *dest, const char *src)
{
    char *dp;
    char *sp = (char *)src;

    if ((dest != NULL) && (src != NULL))
    {
        dp = &dest[strlen(dest)];

        while (*sp != '\0')
        {
            *dp++ = *sp++;
        }
        *dp = '\0';
    }
    return dest;
}

/****************************************************************/
char *
strncat (char *dest, const char *src, int n)
{
    char *dp;
    char *sp = (char *)src;

    if ((dest != NULL) && (src != NULL) && (n > 0))
    {
        dp = &dest[strlen(dest)];

        while ((*sp != '\0') && (n-- > 0))
        {
            *dp++ = *sp++;
        }
        *dp = '\0';
    }
    return dest;
}

/****************************************************************/
char *
strcpy (char *dest, const char *src)
{
    char *dp = (char *)dest;
    char *sp = (char *)src;

    if ((dest != NULL) && (src != NULL))
    {
        while (*sp != '\0')
        {
            *dp++ = *sp++;
        }
        *dp = '\0';
    }
    return dest;
}

/****************************************************************/
char *
strncpy (char *dest, const char *src, int n)
{
    char *dp = (char *)dest;
    char *sp = (char *)src;

    if ((dest != NULL) && (src != NULL) && (n > 0))
    {
        while ((*sp != '\0') && (n-- > 0))
        {
            *dp++ = *sp++;
        }
        *dp = '\0';
    }
    return dest;
}

/****************************************************************/
int
strcmp (const char *s1, const char *s2)
{
    /* No checks for NULL */
    char *s1p = (char *)s1;
    char *s2p = (char *)s2;

    while (*s2p != '\0')
    {
        if (*s1p != *s2p)
            break;

        ++s1p;
        ++s2p;
    }
    return (*s1p - *s2p);
}

/****************************************************************/
int
strncmp (const char *s1, const char *s2, int n)
{
    /* No checks for NULL */
    char *s1p = (char *)s1;
    char *s2p = (char *)s2;

    if (n <= 0)
        return 0;

    while (*s2p != '\0')
    {
        if (*s1p != *s2p)
            break;

        if (--n == 0)
            break;

        ++s1p;
        ++s2p;
    }
    return (*s1p - *s2p);
}

/****************************************************************/
void *
memcpy (void *dest, const void *src, unsigned n)
{
    int longs, bytes;
    uint32 *dpl = (uint32 *)dest;
    uint32 *spl = (uint32 *)src;
    uint8  *dpb, *spb;

    if ((dest != NULL) && (src != NULL) && (n > 0))
    {
        bytes = (n & 0x3);
        longs = (n - bytes) >> 2;
    
        while (longs--)
            *dpl++ = *spl++;
        
        dpb = (uint8 *)dpl;
        spb = (uint8 *)spl;
        
        while (bytes--)
            *dpb++ = *spb++;
    }
    return dest;
}

/****************************************************************/
void *
memset (void *s, int c, unsigned n)
{
    /* Not optimized, but very portable */
    unsigned char *sp = (unsigned char *)s;

    if ((s != NULL) && (n > 0))
    {
        while (n--)
        {
            *sp++ = (unsigned char)c;
        }
    }
    return s;
}

/****************************************************************/
