#include "atsha204a.h" 

u8 __atsha204_reflect8(u8 b) {
    b = (b & 0xF0) >> 4 | (b & 0x0F) << 4;
    b = (b & 0xCC) >> 2 | (b & 0x33) << 2;
    b = (b & 0xAA) >> 1 | (b & 0x55) << 1;
    return b;
}

void __atsha204_crc16(u8 *buffer, u16 len, u8* crc) {
    u8 i, j, n_byte;
    u16 crc_value = 0;

    for (j=0; j<len; j++) {
        n_byte = __atsha204_reflect8(buffer[j]);
        for (i = 0; i < 8; i++) {
            if (((crc_value & 0x8000) >> 8) ^ (n_byte & 0x80))
                crc_value = (crc_value << 1) ^ 0x8005;
            else 
                crc_value = (crc_value << 1);
        
            n_byte <<= 1;
        }
    }   
    
    crc[1] = (crc_value & 0xFF00) >> 8; 
    crc[0] = crc_value & 0x00FF; 
}

u8 __atsha204_crc16_check(u8 *buf, u8 len) {
    u8 crc[2];
    __atsha204_crc16(buf, len-2, crc);
    if ((crc[0] == buf[len-2]) && (crc[1] == buf[len-1])) return 1; else return 0;
}

/* ох и геморный же чип эта atsha204a - вот кто придумал такую инициализацию? Что он курил перед этим? */
void __atsha204a_wakeup(void) {
    u32 i;
    i2c_peripheral_disable(I2C1);

    gpio_mode_setup(GPIOB, GPIO_MODE_OUTPUT, GPIO_MODE_OUTPUT, GPIO9);
    gpio_clear(GPIOB, GPIO9);
    for (i=0; i<100000L; i++) __asm__("NOP");
    gpio_set(GPIOB, GPIO9);
    for (i=0; i<200000L; i++) __asm__("NOP");
    
    start_i2c1();
    for (i=0; i<5000L; i++) __asm__("NOP");
}

u32 i2c_atsha204a_random(u8* buffer) {
    struct dma1_i2c1_transaction i2c_tr;
    u8 status[4];
    u8* tmp;
    u32 i;
    
    i2c_atsha204a_read_status(status, 4);
    
    i2c_tr.dev_addr     = 0xC8 >> 1;
    i2c_tr.read_write   = I2C_WRITE;
    i2c_tr.buffer_count = 8;
    i2c_tr.buffer       = (u8[]) {0x03, 0x07, 0x1b, 0x01, 0x00, 0x00, 0x27, 0x47};
    i2c_tr.last_error   = 0;
    i2c_dma_req(&i2c_tr);
    i2c_write_dma_wait();
    if (i2c_tr.last_error != 0) return i2c_tr.last_error;
    
    for (;;) {
        i2c_tr.dev_addr     = 0xC8 >> 1;
        i2c_tr.read_write   = I2C_READ;
        i2c_tr.buffer_count = 4;
        i2c_tr.buffer       = status;
        i2c_tr.last_error   = 0;
        i2c_dma_req(&i2c_tr);
        i2c_read_dma_wait();
        
        if (i2c_tr.last_error == 0) {
            debug_print_hex(status, 4);
            break;
        }
        
        for (i=0; i<10000L; i++) __asm__("NOP");
        
        //__resync();
    }

    
    /*memset(status, 0, 4);
    tmp = malloc(status[0]);
    memset(tmp, 0, status[0]);
    
    i2c_tr.dev_addr     = 0xC8 >> 1;
    i2c_tr.read_write   = I2C_READ;
    i2c_tr.buffer_count = status[0];
    i2c_tr.buffer       = tmp;
    i2c_tr.last_error   = 0;
    i2c_dma_req(&i2c_tr);
    i2c_read_dma_wait();
    
    debug_print("RD RND ATSHA204A: ");
    debug_print_hex(tmp, status[0]);
    
    free(tmp);
    
    //debug_print_hex(status, 4);
    */
    /*
    
    
    //while (1) {
        i2c_tr.dev_addr     = 0xC8 >> 1;
        i2c_tr.read_write   = I2C_READ;
        i2c_tr.buffer_count = 4;
        i2c_tr.buffer       = status;
        i2c_tr.last_error   = 0;
        i2c_dma_req(&i2c_tr);
        i2c_read_dma_wait();
        //if (i2c_tr.last_error == 0) break;
        ///for (i=0; i<100000L; i++) __asm__("NOP");
        
    //}
    
    debug_print_hex(status, 4);
    
    tmp = malloc(status[0]);
    memset(tmp, 0, status[0]);
    
   
    i2c_tr.dev_addr     = 0xC8 >> 1;
    i2c_tr.read_write   = I2C_READ;
    i2c_tr.buffer_count = status[0]-4;
    i2c_tr.buffer       = tmp+4;
    i2c_tr.last_error   = 0;
    i2c_dma_req(&i2c_tr);
    i2c_read_dma_wait();
    
    memcpy(tmp, status, 4);
    debug_print("RD RND ATSHA204A: ");
    debug_print_hex(tmp, status[0]);
    
    free(tmp);
    */
    
}







u32 i2c_atsha204a_read_status(u8* buffer, u16 count) {
    struct dma1_i2c1_transaction i2c_tr;

    __atsha204a_wakeup();
    
    i2c_tr.dev_addr     = 0xC8 >> 1;
    i2c_tr.read_write   = I2C_WRITE;
    i2c_tr.buffer_count = 1;
    i2c_tr.buffer       = (u8[]) {0};
    i2c_tr.last_error   = 0;
    i2c_dma_req(&i2c_tr);
    i2c_write_dma_wait();
    if (i2c_tr.last_error != 0) return i2c_tr.last_error;
    
    i2c_tr.dev_addr     = 0xC8 >> 1;
    i2c_tr.read_write   = I2C_READ;
    i2c_tr.buffer_count = count;
    i2c_tr.buffer       = buffer;
    i2c_tr.last_error   = 0;
    i2c_dma_req(&i2c_tr);
    i2c_read_dma_wait();
    
    if (i2c_tr.last_error != 0) return i2c_tr.last_error;
    
    if (__atsha204_crc16_check(i2c_tr.buffer, i2c_tr.buffer_count) == 1) {
        debug_print("CRC OK");
        debug_print_hex(buffer, count);
    }else {
        debug_print("CRC ERROR");
        u8 crc1[2];
        __atsha204_crc16(i2c_tr.buffer, i2c_tr.buffer_count-2, crc1);
        
        debug_print_hex(crc1, 2);
        debug_print_hex((u8[]) {i2c_tr.buffer[i2c_tr.buffer_count-2], i2c_tr.buffer[i2c_tr.buffer_count-1]}, 2);
    }
    
    return 0;
}


























