// See LICENSE file for licence details

#ifndef N200_ECLIC_H
#define N200_ECLIC_H

  // Need to know the following info from the soc.h, so include soc.h here
  //   SOC_ECLIC_CTRL_ADDR      : what is the base address of ECLIC in this SoC
  //   SOC_ECLIC_NUM_INTERRUPTS : how much of irq configured in total for the ECLIC in this SoC
#include "soc/drivers/soc.h"

#define CLICINTCTLBITS  3

//ECLIC memory map
//   Offset
//  0x0000       1B          RW        cliccfg
#define ECLIC_CFG_OFFSET            0x0
//  0x0004       4B          R         clicinfo   
#define ECLIC_INFO_OFFSET           0x4
//  0x000B       1B          RW        mintthresh 
#define ECLIC_MTH_OFFSET         0xB
//
//  0x1000+4*i   1B/input    RW        clicintip[i]
#define ECLIC_INT_IP_OFFSET            _AC(0x1000,UL)
//  0x1001+4*i   1B/input    RW        clicintie[i]
#define ECLIC_INT_IE_OFFSET            _AC(0x1001,UL)
//  0x1002+4*i   1B/input    RW        clicintattr[i]
#define ECLIC_INT_ATTR_OFFSET          _AC(0x1002,UL)

#define ECLIC_INT_ATTR_SHV              0x01
#define ECLIC_INT_ATTR_TRIG_LEVEL       0x00
#define ECLIC_INT_ATTR_TRIG_EDGE        0x02
#define ECLIC_INT_ATTR_TRIG_POS         0x00
#define ECLIC_INT_ATTR_TRIG_NEG         0x04

//  0x1003+4*i   1B/input    RW        clicintctl[i]
#define ECLIC_INT_CTRL_OFFSET          _AC(0x1003,UL)
//
//  ...
//
#define ECLIC_ADDR_BASE           SOC_ECLIC_CTRL_ADDR

#define ECLIC_NUM_INTERRUPTS (SOC_ECLIC_NUM_INTERRUPTS + 19)

#define ECLIC_CFG_NLBITS_MASK          _AC(0x1E,UL)
#define ECLIC_CFG_NLBITS_LSB     (1u)

#define ECLIC_INT_MSIP          3
#define ECLIC_INT_MTIP          7
#define ECLIC_INT_MEIP          11

#define BUTTON_1_HANDLER eclic_irq49_handler

#define MTIME_HANDLER   eclic_mtip_handler
#define SSIP_HANDLER    eclic_ssip_handler 
#define MSIP_HANDLER    eclic_msip_handler 
#define UTIP_HANDLER    eclic_utip_handler 
#define STIP_HANDLER    eclic_stip_handler 
#define IRQ7_HANDLER    eclic_irq7_handler 
#define UEIP_HANDLER    eclic_ueip_handler 
#define SEIP_HANDLER    eclic_seip_handler 
#define MEIP_HANDLER    eclic_meip_handler 
#define IMECCI_HANDLER  eclic_imecci_handler
#define BWEI_HANDLER    eclic_bwei_handler
#define PMOVI_HANDLER   eclic_pmovi_handler


#endif

