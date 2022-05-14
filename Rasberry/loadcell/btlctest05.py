import time
import sys
from bluetooth import *




#lcsc

EMULATE_HX711=False

referenceUnit = 1

if not EMULATE_HX711:
    import RPi.GPIO as GPIO
    from hx711 import HX711
else:
    from emulated_hx711 import HX711

def cleanAndExit():
    print("Cleaning...")

    if not EMULATE_HX711:
        GPIO.cleanup()
        
    print("Bye!")
    sys.exit()

hx = HX711(20, 16)


hx.set_reading_format("MSB", "MSB")

hx.set_reference_unit(387)


hx.reset()

hx.tare()

print("Tare done! Add weight now...")

def bluesoc():
    global client_socket
    #btsc
    server_socket= BluetoothSocket(RFCOMM)
    print(server_socket)
    port = 1
    server_socket.bind(("", port))
    server_socket.listen(1)

    client_socket, address = server_socket.accept()
    print("Accepted connection from ", address)
                          

bluesoc()

while True:
        try:

            val = hx.get_weight(5)
        
            client_socket.send("%d" %val)
                
            data = client_socket.recv(1024)
            
            print(val)
            
            
            
            hx.power_down()
            hx.power_up()
            time.sleep(0.1)

        except (KeyboardInterrupt, SystemExit):
            cleanAndExit()
        except IOError:
            bluesoc()