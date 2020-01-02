import spidev
import time

spi = spidev.SpiDev()
spi.open(0,0)
spi.max_speed_hz=1350000
reading1 = 2;
def analog(channel):
    r = spi.xfer2([1,(8+channel)<<4,0])
    adc_out = ((r[1]&3)<<8)+r[2]
    return adc_out


reading = analog(0)
   
if reading is not None:
    time.sleep(2)

#time.sleep(10)