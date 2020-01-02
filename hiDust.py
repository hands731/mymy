from __future__ import print_function
import serial, struct, sys, time, json

DEBUG = 0
CMD_MODE = 2
CMD_QUERY_DATA = 4
CMD_DEVICE_ID = 5
CMD_SLEEP = 6
CMD_FIRMWARE = 7
CMD_WORKING_PERIOD = 8
MODE_ACTIVE = 0
MODE_QUERY = 1

ser = serial.Serial()
ser.port = "/dev/ttyUSB0"
ser.baudrate = 9600

ser.open()
ser.flushInput()
value2 = 7
byte, data = 0, ""

def read_response():
    byte = 0
    while byte != "\xaa":
        byte = ser.read(size=1)
    d = ser.read(size=9)        
    return byte + d

def construct_command(cmd, data=[]):
    assert len(data) <= 12
    data += [0,]*(12-len(data))
    checksum = (sum(data)+cmd-2)%256
    ret = "\xaa\xb4" + chr(cmd)
    ret += ''.join(chr(x) for x in data)
    ret += "\xff\xff" + chr(checksum) + "\xab"

    return ret

def process_data(d):
    r = struct.unpack('<HHxxBB', d[2:])
    pm25 = r[0]/10.0
    pm10 = r[1]/10.0
    checksum = sum(ord(v) for v in d[2:8])%256
    return [pm25, pm10]
  
def cmd_query_data():
    ser.write(construct_command(CMD_QUERY_DATA))
    d = read_response()
    values = []
    if d[1] == "\xc0":
        values = process_data(d)
    return values

if __name__ == "__main__":
    while True:
        
        for t in range(100):
            values = cmd_query_data();
            if values is not None:
                print("PM2.5: ", values[0], ", PM10: ", values[1])
                time.sleep(0.1)
                
        print("Going to sleep for 1 min...")
        time.sleep(60)
