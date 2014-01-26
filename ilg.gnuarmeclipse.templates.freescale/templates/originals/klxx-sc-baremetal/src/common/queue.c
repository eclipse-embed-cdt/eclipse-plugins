/*
 * File:    queue.c
 * Purpose: Implement a first in, first out linked list
 *
 * Notes:   
 */

#include "common.h"
#include "queue.h"

/********************************************************************/
/* 
 * Initialize the specified queue to an empty state
 * 
 * Parameters:
 *  q   Pointer to queue structure
 */
void
queue_init(QUEUE *q)
{
    q->head = NULL;
}
/********************************************************************/
/* 
 * Check for an empty queue
 *
 * Parameters:
 *  q       Pointer to queue structure
 * 
 * Return Value:
 *  1 if Queue is empty
 *  0 otherwise
 */
int
queue_isempty(QUEUE *q)
{
    return (q->head == NULL);
}
/********************************************************************/
/* 
 * Add an item to the end of the queue 
 *
 * Parameters:
 *  q       Pointer to queue structure
 *  node    New node to add to the queue
 */
void
queue_add(QUEUE *q, QNODE *node)
{
    if (queue_isempty(q))
    {
        q->head = q->tail = node;
    }
    else
    {
        q->tail->next = node;
        q->tail = node;
    }
    
    node->next = NULL;
}

/********************************************************************/
/* 
 * Remove and return first (oldest) entry from the specified queue 
 *
 * Parameters:
 *  q       Pointer to queue structure
 *
 * Return Value:
 *  Node at head of queue - NULL if queue is empty
 */
QNODE*
queue_remove(QUEUE *q)
{
    QNODE *oldest;
    
    if (queue_isempty(q))
        return NULL;
    
    oldest = q->head;
    q->head = oldest->next;
    return oldest;
}
/********************************************************************/
/* 
 * Peek into the queue and return pointer to first (oldest) entry.
 * The queue is not modified
 *
 * Parameters:
 *  q       Pointer to queue structure
 *
 * Return Value:
 *  Node at head of queue - NULL if queue is empty
 */
QNODE*
queue_peek(QUEUE *q)
{
    return q->head;
}
/********************************************************************/
/* 
 * Move entire contents of one queue to the other
 *
 * Parameters:
 *  src     Pointer to source queue
 *  dst     Pointer to destination queue
 */
void
queue_move(QUEUE *dst, QUEUE *src)
{
    if (queue_isempty(src))
        return;
    
    if (queue_isempty(dst))
        dst->head = src->head;
    else
        dst->tail->next = src->head;

    dst->tail = src->tail;
    src->head = NULL;
    return;
}
/********************************************************************/
