//  Nilheim Mechatronics Simplified Eye Mechanism Code
//  Make sure you have the Adafruit servo driver library installed >>>>> https://github.com/adafruit/Adafruit-PWM-Servo-Driver-Library
//  X-axis joystick pin: A1
//  Y-axis joystick pin: A0
//  Trim potentiometer pin: A2
//  Button pin: 2

 
#include <Wire.h>
#include <Adafruit_PWMServoDriver.h>
#include <Adafruit_NeoPixel.h>

Adafruit_PWMServoDriver pwm = Adafruit_PWMServoDriver();

#define SERVOMIN  140 // this is the 'minimum' pulse length count (out of 4096)
#define SERVOMAX  520 // this is the 'maximum' pulse length count (out of 4096)
#define LED_PIN    7
#define LED_COUNT 1


// our servo # counter
uint8_t servonum = 0;
Adafruit_NeoPixel strip(LED_COUNT, LED_PIN, NEO_GRB + NEO_KHZ800);



String msg;
String check;
String setx;
String sety;
int counter = 0;

void setup() {
  Serial.begin(9600);
  Serial.println("8 channel Servo test!");
  strip.begin();
  strip.setBrightness(64);

  strip.show(); // Initialize all pixels to 'off'
  pwm.begin();
  
  pwm.setPWMFreq(60);  // Analog servos run at ~60 Hz updates

  delay(10);
}

// you can use this function if you'd like to set the pulse length in seconds
// e.g. setServoPulse(0, 0.001) is a ~1 millisecond pulse width. its not precise!
void setServoPulse(uint8_t n, double pulse) {
  double pulselength;
  
  pulselength = 1000000;   // 1,000,000 us per second
  pulselength /= 60;   // 60 Hz
  Serial.print(pulselength); Serial.println(" us per period"); 
  pulselength /= 4096;  // 12 bits of resolution
  Serial.print(pulselength); Serial.println(" us per bit"); 
  pulse *= 1000000;  // convert to us
  pulse /= pulselength;
  Serial.println(pulse);

}

void loop() {
  if (Serial.available()) {
  counter = 0;
  setx = "";
  sety = "";
  check = "";
  while (counter < 7){
    if(counter == 0 ){
    check += (char)Serial.read();
    delay(5);
              
    }
    else if (counter > 0 && counter <4){
            setx += (char)Serial.read();
       delay(5); 
    }
    else{
   sety += (char)Serial.read();
     delay(5);
  }
  counter = counter+1;
  }
  Serial.flush();
  Serial.print(check);
  Serial.print(" ");
  Serial.print(setx);
  Serial.print(" ");
  Serial.print(sety);  
  Serial.print("\n");
  switch (check.toInt()){
    case 1:
      pwm.setPWM(0, 0, setx.toInt());
      pwm.setPWM(1, 0, sety.toInt());
      break;
    case 4:
      pwm.setPWM(2, 0, 400);
      pwm.setPWM(3, 0, 240);
      pwm.setPWM(4, 0, 240);
      pwm.setPWM(5, 0, 400);
      strip.clear();
      strip.show(); // Initialize all pixels to 'off'
      break;
    case 5:
      pwm.setPWM(4, 0, 400); //prawegora
      pwm.setPWM(5, 0, 220); //prawedol
      pwm.setPWM(2, 0, 220); // lewegora
      pwm.setPWM(3, 0, 400); // lewedol
      strip.setPixelColor(0, 255, 0, 0);
      strip.show(); // Initialize all pixels to 'off'
      break;
    default:
      break;
  }
  delay(5);
}

}
