import RPi.GPIO as GPIO
import time

  
 
def humidyMotor1(c1,c2,s):
    print("iiiiiiiiiii")
#3056 
    
    for i in range (24):
        for halfstep in range (8):
            for pin in range (4):
                GPIO.output(c2[pin], s[halfstep][pin])
                time.sleep(0.001)
                
 
                
                 

  