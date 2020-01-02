import RPi.GPIO as GPIO
import time

  

def closeMotor(c2,s):     


    for i in range (400):
        for halfstep in range (8):
            for pin in range (4):
                GPIO.output(c2[pin], s[halfstep][pin])
                time.sleep(0.001)
                