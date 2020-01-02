import RPi.GPIO as GPIO
import time
   
   

def openMotor(c1,s):     
#3056 
    for i in range (400):
        for halfstep in range (8):
            for pin in range (4):
                GPIO.output(c1[pin], s[halfstep][pin])
                time.sleep(0.001)

