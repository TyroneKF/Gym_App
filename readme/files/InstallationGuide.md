[//]: <> (#################################################################################.)
<!--- Main Title -->

<h1 align="center"> 💾 Installation Guide </h1>

This application can be installed in two ways:

- **1.) Recommended:** Install using the packaged .exe installer (no setup required)
- **2.)** Manual: Run the application using Java 21 and the .jar file

[//]: <> (#################################################################################.)
##
<!--- Method 1 JPackage -->

<h2 align="left"> ✅ Method 1 — Install via Executable (Recommended) </h2>

This is the easiest and preferred method. No Java installation is required.

<h5 align="left"> 🔽 Steps </h5>

- **1.)**  Go to the releases page:
      - 👉 https://github.com/TyroneKF/Gym_App/releases
- **2.)**  Download the latest installer:
      - File format: .exe
- **3.)**  Run the installer:
      - Double-click the downloaded .exe
      - Follow the installation prompts
- **4.)**  After installation:
      - The application will be installed to:
        - C:\Program Files\GymApp
- **5.)**  Launch the application:
    - Navigate to the install directory, or
    - Use the Start Menu shortcut (if enabled)

Main executable:
- GymApp.exe

<h5 align="left"> 📦 What This Method Does </h5>  

- Bundles the required Java runtime internally
- Installs the application system-wide
- Creates a native Windows executable
- Requires **no manual configuration**

[//]: <> (#################################################################################.)
##
<!--- Method 2 Manual -->

<h2 align="left"> ⚙️ Method 2 — Manual Setup (Java + JAR) </h2>
Use this method if you prefer full control or do not trust packaged installers.


<h5 align="left"> 🔧 Step 1 — Install Java 21 </h5>  

You must install Java 21 before running the application.

Recommended distributions:
- Eclipse Adoptium (Temurin JDK)
- Oracle JDK

<br>

Download and install Java 21, then verify:

java -version

<br>

Expected output should include:

21.x.x

[//]: <> (#################################################################################.)
##
<!--- Step 2 -->

<h5 align="left"> 📥 Step 2 — Download the JAR </h5>

- **1.)** Go to the releases page:
  - 👉 https://github.com/TyroneKF/Gym_App/releases
  
- **2.)** Download the .jar file from the latest release

[//]: <> (#################################################################################.)
##
<!--- Step 3 -->

<h5 align="left">  ▶️ Step 3 — Run the Application </h5>

Navigate to the folder containing the .jar file and run:

java -jar GymApp.jar


[//]: <> (#################################################################################.)
##
<!--- Step 3 -->

<h5 align="left">  ⚠️ Optional (Recommended) — Set JAVA_HOME </h5>

For consistent behaviour:

**Windows:**

- **1.)** Open Environment Variables
- **2.)** Add:
  - JAVA_HOME = C:\Program Files\Java\jdk-21
  
- **3.)** Add to Path:
  - %JAVA_HOME%\bin


[//]: <> (#################################################################################.)
##
<!--- Comparison -->

<h5 align="left">  🆚 Comparison — Set JAVA_HOME </h5>

<img width="822" height="336" alt="Capture" src="https://github.com/user-attachments/assets/8eefcb72-aec0-4999-a4ac-3c11dc113b44" />


[//]: <> (#################################################################################.)
##
<!--- Troubleshooting -->

<h2 align="left">  🛠️ Troubleshooting </h2>

**App won’t start (Manual method)**
- Ensure Java 21 is installed
- Run java -version to confirm
- Verify the .jar file is not corrupted

**Permission issues (Installer)**
- Run installer as Administrator
-  Ensure access to C:\Program Files
  
**Java not recognised**
- Check PATH and JAVA_HOME configuration

[//]: <> (#################################################################################.)
##
<!--- Summary -->

<h2 align="left"> 📌 Summary </h2>

- Use the .exe installer for the simplest experience
- Use the manual Java method for full control and transparency

