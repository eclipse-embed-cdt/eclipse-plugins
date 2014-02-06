/*
 * File:        alloc.c
 * Purpose:     generic malloc() and free() engine
 *
 *
 */

#include "common.h"
#include "stdlib.h"

#ifndef KEIL
#pragma section = "HEAP"
#endif

/********************************************************************/

/*
 * This struct forms the minimum block size which is allocated, and
 * also forms the linked list for the memory space used with alloc()
 * and free().  It is padded so that on a 32-bit machine, all malloc'ed
 * pointers are 16-byte aligned.
 */
typedef struct ALLOC_HDR
{
    struct
    {
        struct ALLOC_HDR     *ptr;
        unsigned int size;
    } s;
    unsigned int align;
    unsigned int pad;
} ALLOC_HDR;

static ALLOC_HDR base;
static ALLOC_HDR *freep = NULL;

/********************************************************************/
void
free (void *ap)
{
    ALLOC_HDR *bp, *p;

    bp = (ALLOC_HDR *)ap - 1;   /* point to block header */
    for (p = freep; !((bp > p) && (bp < p->s.ptr)) ; p = p->s.ptr)
    {
        if ((p >= p->s.ptr) && ((bp > p) || (bp < p->s.ptr)))
        {
            break; /* freed block at start or end of arena */
        }
    }

    if ((bp + bp->s.size) == p->s.ptr)
    {
        bp->s.size += p->s.ptr->s.size;
        bp->s.ptr = p->s.ptr->s.ptr;
    }
    else
    {
        bp->s.ptr = p->s.ptr;
    }

    if ((p + p->s.size) == bp)
    {
        p->s.size += bp->s.size;
        p->s.ptr = bp->s.ptr;
    }
    else
    {
        p->s.ptr = bp;
    }

    freep = p;
}

/********************************************************************/
void *
malloc (unsigned nbytes)
{
    /* Get addresses for the HEAP start and end */
    #if defined(CW)  
      extern char __HEAP_START[];
      extern char __HEAP_END[];
    #elif defined(IAR)
      char* __HEAP_START = __section_begin("HEAP");
      char* __HEAP_END = __section_end("HEAP");
    #elif defined(KEIL)
	  extern uint32_t HEAP$$Base;
	  extern uint32_t HEAP$$Limit;
	  uint32_t __HEAP_START = (uint32_t)&HEAP$$Base;
	  uint32_t __HEAP_END = (uint32_t)&HEAP$$Limit;
    #endif
   
    ALLOC_HDR *p, *prevp;
    unsigned nunits;

    nunits = ((nbytes+sizeof(ALLOC_HDR)-1) / sizeof(ALLOC_HDR)) + 1;

    if ((prevp = freep) == NULL)
    {
        p = (ALLOC_HDR *)__HEAP_START;
        p->s.size = ( ((uint32)__HEAP_END - (uint32)__HEAP_START)
            / sizeof(ALLOC_HDR) );
        p->s.ptr = &base;
        base.s.ptr = p;
        base.s.size = 0;
        prevp = freep = &base;
    }

    for (p = prevp->s.ptr; ; prevp = p, p = p->s.ptr)
    {
        if (p->s.size >= nunits)
        {
            if (p->s.size == nunits)
            {
                prevp->s.ptr = p->s.ptr;
            }
            else
            {
                p->s.size -= nunits;
                p += p->s.size;
                p->s.size = nunits;
            }
            freep = prevp;
            return (void *)(p + 1);
        }

        if (p == freep)
            return NULL;
    }
}

/********************************************************************/
