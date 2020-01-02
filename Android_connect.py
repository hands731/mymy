import socket
import lcddriver
import RPi.GPIO as GPIO
import dht11
import time
import datetime
import dustValues as dustFile
import threading
import uv
import cuttonMotorOpen
import cuttonMotorClose
import humidyOn
import humidyOff
GPIO.setwarnings(False)
GPIO.setmode(GPIO.BCM)
GPIO.setup(18,GPIO.OUT)
GPIO.setup(21,GPIO.OUT)
GPIO.setup(23,GPIO.OUT)
GPIO.output(18,True)
GPIO.output(23,True)
display1 = lcddriver.lcd()

ControlPin = [5,22,17,4]  #ControlPin4 = [5,22,17,4]
ControlPin2 = [4,17,22,5]
humidyPin = [6,13,19,26]
humidyPin2 = [26,19,13,6]
for pin in ControlPin2:
    GPIO.setup(pin, GPIO.OUT)
    GPIO.output(pin, 0)
      
    seq=[[1,0,0,0],
         [1,1,0,0],
         [0,1,0,0],
         [0,1,1,0],
         [0,0,1,0],
         [0,0,1,1],
         [0,0,0,1],
         [1,0,0,1] ]
for pin in ControlPin:
    GPIO.setup(pin, GPIO.OUT)
    GPIO.output(pin, 0)
      
    seq=[[1,0,0,0],
         [1,1,0,0],
         [0,1,0,0],
         [0,1,1,0],
         [0,0,1,0],
         [0,0,1,1],
         [0,0,0,1],
         [1,0,0,1] ]

for pin in humidyPin:
    GPIO.setup(pin, GPIO.OUT)
    GPIO.output(pin, 0)
      
    seq=[[1,0,0,0],
         [1,1,0,0],
         [0,1,0,0],
         [0,1,1,0],
         [0,0,1,0],
         [0,0,1,1],
         [0,0,0,1],
         [1,0,0,1] ]
    
for pin in humidyPin2:
    GPIO.setup(pin, GPIO.OUT)
    GPIO.output(pin, 0)
      
    seq=[[1,0,0,0],
         [1,1,0,0],
         [0,1,0,0],
         [0,1,1,0],
         [0,0,1,0],
         [0,0,1,1],
         [0,0,0,1],
         [1,0,0,1] ]
time.sleep(2)

instance = dht11.DHT11(pin=27) 



HOST = "192.168.0.26"
PORT = 12346
print(HOST)
a = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
print('Socket created')
a.bind((HOST,PORT))
print('Socket bind complete')
a.listen(1)
print('Socket now listening')

save = ""
count = 0
def do_display(display_string):
    global save
    global display1

  
    try:
        newList = save.split(':')
        if(len(newList)>8):
        
            new1List = newList[4].split('H')
            new2List = newList[5].split('P')
            new3List = newList[6].split('P')
            new4List = newList[7].split('u')
           
            while True:
                display1.lcd_display_string("Temp:"+new1List[0]+"uv:"+newList[8],1)
                display1.lcd_display_string("Humidity : "+new2List[0]+"  ",2)
                time.sleep(15) 
                display1.lcd_clear()                               # Clear the display of any data
                time.sleep(1)   
                display1.lcd_display_string("PM2.5 : "+new3List[0],1)
                display1.lcd_display_string("PM10  : "+new4List[0],2)
                time.sleep(15) 
                display1.lcd_clear()                               # Clear the display of any data
                time.sleep(2)
        else:
            while True:
                display1.lcd_clear()                               # Clear the display of any data
                time.sleep(1)
    except KeyboardInterrupt: # If there is a KeyboardInterrupt (when you press ctrl+c), exit the program and cleanup
        print("Cleaning up!")
        display1.lcd_clear()       
                
def do_some_stuffs_with_input(input_string):
    global save
    if input_string == "loading...":
        dustValue = []
        dustValue = dustFile.values1
        result = instance.read()
        
        if result.is_valid():
            temp = str(result.temperature)
            hum = str(result.humidity)
            uvValue = str(uv.analog(0))
            
        
            input_string = "Today : " + str(datetime.datetime.now())+"\n" +" Temperature :" + temp + "'C  Humidity :" + hum +"%\n" +"PM2.5:"+ str(dustValue[0]) + "mg/m3  PM10:"+ str(dustValue[1])+ "mg/m3"+"\n"+"uv :"+uvValue+"Level"
           
            save= input_string
  
    elif input_string == "off":
         GPIO.output(23,True)
         GPIO.output(18,True)
         input_string = "OFF"
        
    elif input_string == "airCleanerOn":
        GPIO.output(23,False)
        input_string = save+"&airCleanerOn"
    
    elif input_string == "airCleanerOff":
        GPIO.output(23,True)
        input_string = save+"&airCleanerOff"
        
    elif input_string == "airConditionOn":
        input_string = save+"&airConditionOn"
        
        
    elif input_string == "airConditionOff":
        input_string = save+"&airConditionOff"
         
    elif input_string == "cuttonOn":
        cuttonMotorOpen.openMotor(ControlPin2,seq)
        input_string = save+"&CuttonOn"
        
       
    
    elif input_string == "cuttonOff":
    
        cuttonMotorClose.closeMotor(ControlPin,seq)
        input_string = save+"&cuttonOff"
        
    elif input_string == "humidyOn":
        GPIO.output(18,False)
        humidyOn.humidyMotor1(humidyPin,humidyPin2,seq)
        
        input_string = save+"&humidyOn"
        
        
    elif input_string == "humidyOff":
        GPIO.output(18,True)
        humidyOff.humidyMotor1(humidyPin,humidyPin2,seq)
        input_string = save+"&humidyOff"
    else:
        input_string = input_string + " failed commandline"
    return input_string

while True:
    
    conn, addr = a.accept()
    print("Connected by ",addr)
    
    #data susin
    data = conn.recv(1024)
    data = data.decode("utf8").strip()
    if not data: break
    print("Received: " + data)
    
     
    
    threading._start_new_thread(do_some_stuffs_with_input,(data,))
    time.sleep(1)

    
    #susin data pi control
    res = do_some_stuffs_with_input(data)
    print("count : "+str(count))
    if count == 0:
        print("start display  : " + str(count) )
        threading._start_new_thread(do_display,(res,))
        print("countValue : " + str(count))
        count = count+1
   
    print("Request : "+res)
    
    #android songsin
    conn.sendall(res.encode("utf-8"))
    
    conn.close()
a.close()

