/*
 * This file is part of the µOS++ distribution.
 *   (https://github.com/micro-os-plus)
 * Copyright (c) 2017 Liviu Ionescu.
 *
 * Permission is hereby granted, free of charge, to any person
 * obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without
 * restriction, including without limitation the rights to use,
 * copy, modify, merge, publish, distribute, sublicense, and/or
 * sell copies of the Software, and to permit persons to whom
 * the Software is furnished to do so, subject to the following
 * conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 */

#ifndef MICRO_OS_PLUS_STARTUP_INITIALIZE_HOOKS_H_
#define MICRO_OS_PLUS_STARTUP_INITIALIZE_HOOKS_H_

#include <stddef.h>
#include <stdbool.h>

#if defined(__cplusplus)
extern "C"
{
#endif /* __cplusplus */

  /**
   * @addtogroup micro-os-plus-app-hooks
   * @{
   */

  /**
   * @name Startup Routines
   * @{
   */

  /**
   * @brief Initialise hardware early.
   * @par Parameters
   *  None.
   * @par Returns
   *  Nothing.
   */
  void
  os_startup_initialize_hardware_early (void);

  /**
   * @brief Initialise hardware.
   * @par Parameters
   *  None.
   * @par Returns
   *  Nothing.
   */
  void
  os_startup_initialize_hardware (void);

  /**
   * @brief Initialise free store.
   * @param heap_address The first unallocated RAM address (after the BSS).
   * @param heap_size_bytes The free store size.
   * @par Returns
   *  Nothing.
   */
  void
  os_startup_initialize_free_store (void* heap_address, size_t heap_size_bytes);

  /**
   * @brief Initialise arguments.
   * @param [out] p_argc Pointer to argc.
   * @param [out] p_argv Pointer to argv.
   */
  void
  os_startup_initialize_args (int* p_argc, char*** p_argv);

  /**
   * @}
   */

  /**
   * @name Termination Routines
   * @{
   */

  /**
   * @brief Display statistics and say goodbye before terminating.
   * @par Parameters
   *  None.
   * @par Returns
   *  Nothing.
   */
  void
  os_terminate_goodbye (void);

  /**
   * @brief Terminate the application. There is no more life after this.
   * @param [in] code Exit code, 0 for success, non 0 for failure.
   * @par Returns
   *  Nothing.
   */
  void
  __attribute__ ((noreturn))
  os_terminate (int code);

/**
 * @}
 */

/**
 * @}
 */

#if defined(__cplusplus)
}
#endif /* __cplusplus */

#endif /* MICRO_OS_PLUS_STARTUP_INITIALIZE_HOOKS_H_ */
