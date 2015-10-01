/**
 *****************************************************************************
   @file     DmaLib.h
   @brief   Set of DMA peripheral functions.
   - DmaBase(), DmaSet(), DmaClr(), DmaSta() and DmaErr() apply to all DMA channels together.
   - DmaOn() apply for each channel separately. 
   
   @version    V0.1
   @author     ADI
   @date       October 2012


All files for ADuCM360/361 provided by ADI, including this file, are
provided  as is without warranty of any kind, either expressed or implied.
The user assumes any and all risk from the use of this code.
It is the responsibility of the person integrating this code into an application
to ensure that the resulting application performs as required and is safe.
**/
#include <ADuCM360.h>

typedef struct
{
   unsigned int cycle_ctrl    :3;
   unsigned int next_useburst :1;
   unsigned int n_minus_1     :10;
   unsigned int r_power       :4;
   unsigned int src_prot_ctrl :3;
   unsigned int dst_prot_ctrl :3;
   unsigned int src_size      :2;
   unsigned int src_inc       :2;
   unsigned int dst_size      :2;
   unsigned int dst_inc       :2;
} CtrlCfgBits;

// Define the structure of a DMA descriptor.
typedef struct dmaDesc
{
   unsigned int srcEndPtr;
   unsigned int destEndPtr;
   union
   {
      unsigned int   ctrlCfgVal;
      CtrlCfgBits    Bits;
   } ctrlCfg ;
   unsigned int reserved4Bytes;
} DmaDesc;
//typedef enum {false = 0, true = !false} boolean;
// Suitable aLignment for the DMA descriptors
#define DMACHAN_DSC_ALIGN 0x200
/* CCD allocation must be an integral power of two, i.e., 12 channels is allocated as 16 */
#define CCD_SIZE                       (16)

extern int DmaBase(void);
extern DmaDesc * Dma_GetDescriptor(unsigned int iChan,int iAlternate);
extern int DmaSet(int iMask, int iEnable, int iAlt, int iPriority);
extern int DmaClr(int iMask, int iEnable, int iAlt, int iPriority);
extern int DmaSta(void);
extern int DmaErr(int iErrClr);

extern int AdcDmaReadSetup(int iType, int iCfg, int iNumVals, int *pucRX_DMA);
extern int AdcDmaWriteSetup(int iType, int iCfg, int iNumVals, int *pucTX_DMA);
extern int DacDmaWriteSetup(int iType, int iCfg, int iNumVals, int *pucTX_DMA);

extern int DmaPeripheralStructSetup(int iChan, int iCfg);
extern int DmaStructPtrOutSetup(int iChan, int iNumVals, unsigned char *pucTX_DMA);
extern int DmaStructPtrInSetup(int iChan, int iNumVals, unsigned char *pucRX_DMA);
extern int DmaCycleCntCtrl(unsigned int iChan, int iNumx, int iCfg);
//DMA channel numbers.
#define	SPI1TX_C	1
#define	SPI1RX_C	2
#define	UARTTX_C	3
#define	UARTRX_C	4
#define	I2CSTX_C	5
#define	I2CSRX_C	6
#define	I2CMTX_C	7
#define	I2CMRX_C	8
#define	DAC_C		9
#define	ADC0_C	10
#define	ADC1_C	11
#define	SINC2_C	12

//DMA channel bit value.
#define	NONE		0
#define	SPI1TX_B	1
#define	SPI1RX_B	2
#define	UARTTX_B	4
#define	UARTRX_B	8
#define	I2CSTX_B	0x10
#define	I2CSRX_B	0x20
#define	I2CMTX_B	0x40
#define	I2CMRX_B	0x80
#define	DAC_B		0x100
#define	ADC0_B		0x200
#define	ADC1_B		0x400
#define	SYNC2_B		0x800

#define ADC0DMAWRITE 0
#define ADC0DMAREAD  1
#define ADC1DMAWRITE 2
#define ADC1DMAREAD  3
#define SINC2DMAREAD 4

//DMA error bit.
#define	DMA_ERR_RD		0
#define	DMA_ERR_CLR		1

//DMA type (Cycl_ctrl)
#define	DMA_STOP		0
#define	DMA_BASIC		1
#define	DMA_AUTO		2
#define	DMA_PING		3
#define	DMA_MSG_PRI		4
#define	DMA_MSG_ALT		5
#define	DMA_PSG_PRI		6
#define	DMA_PSG_ALT		7

//Bytes per transfer.
#define	DMA_SIZE_BYTE		 0
#define	DMA_SIZE_HWORD	 0x1000000
#define	DMA_SIZE_WORD		 0x2000000


//Destination Increment per transfer.
#define	DMA_DSTINC_BYTE		0
#define	DMA_DSTINC_HWORD	0x40000000
#define	DMA_DSTINC_WORD		0x80000000
#define	DMA_DSTINC_NO		  0xC0000000

//Source Increment per transfer.
#define	DMA_SRCINC_BYTE		0
#define	DMA_SRCINC_HWORD	0x4000000
#define	DMA_SRCINC_WORD		0x8000000
#define	DMA_SRCINC_NO		  0xC000000

#define iPrimary        0
#define ALTERNATE      CCD_SIZE



