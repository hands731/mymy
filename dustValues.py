import hiDust as A
import serial

ser = serial.Serial()
ser.port = "/dev/ttyUSB0"
ser.baudrate = 9600

ser.open()
ser.flushInput()
values1 = []
values1 = A.cmd_query_data()
