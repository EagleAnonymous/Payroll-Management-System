
# Java Swing Payroll System (MySQL)

**How to run**

1. Install JDK 17+ and Maven.
2. Create the database:
   ```sql
   SOURCE sql/schema.sql;
   ```
3. Update DB credentials in `src/main/java/com/via/vix/payroll/db/DB.java` if needed.
4. Build and run:
   ```bash
   mvn -q -e -DskipTests package
   mvn -q exec:java -Dexec.mainClass="com.via.vix.payroll.Main"
   ```
   Or run from your IDE: right-click `Main.java` → Run.
5. Login with **username: admin** and **password: root**.

**Notes**

- Payslip/Report PDFs are saved to your `Documents` folder by default.
- If printing to PDF isn't available on your OS, PDF files are still saved using OpenPDF.
