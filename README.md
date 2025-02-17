## MongoDB Lab

For this lab, I made a very simple program that counts the amount of times you press the spacebar within 10 seconds. The score is appended to a MongoDB database.

Running this program should be straightforward. MongoDB automatically creates the database and collection if they are not already created. These are created inside of the `MongoConnection` class.

As long as you have MongoDB installed and configured on **Port 27017**, the program should configure everything for you and not require any additional steps.

Run the `Game` class and try to beat my high score!

---
### IF YOU ARE HAVING ISSUES (WINDOWS):
If you are having any port listening conflicts, here is what I have done in the past to resolve this issue:

1. Open **Command Prompt** as an **administrator**.
2. Run the command `netstat -ano | findstr :27017`. This will display the current processes running on the target port.
   - If nothing is displayed, then MongoDB is either not currently running, OR it is listening on a different port number. If you think MongoDB is not running, skip ahead to step 4. If you suspect it is running on a different port, either configure the code to listen on the port you have MongoDB running on, or reconfigure MongoDB to listen to the default port, 27017.
3. Locate the **process ID** number. This is located to the right of the port listening status. See the circled number in the screenshot. Run the command `taskkill /PID <PROCESS ID> /F` and replace `<PROCESS ID>` with your process ID. ![image](https://github.com/user-attachments/assets/fe7cf722-86f8-46b1-8043-a6e3845a897e)
4. Press `Windows+R` and enter `services.msc`. Navigate to **MongoDB Server** and press "Start the service".
5. Try running the program again.
