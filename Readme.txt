Login Secrets
+--------------------------------------------------+
This program makes a password vault.

+-------------------------
| Setup:
+-------------------------
Download the jar file.

+-------------------------
| Usage:
+-------------------------
Just double click the jar file to execute it.
If that doesn't work, run the command:
	java LoginSecrets.jar

1. When you start the program, a welcome screen will appear.
	You can choose whether to make a Vault file, or load an existing one.
2. On the next screen, pick a Vault file to open/create.
	Then click "Submit".
3. The third screen will ask you for a password.
	a) If you're making a new Vault, type in the password you want to use.
		On this screen, you can also pick a Keyfile.
	b) If you're opening a Vault, type in the proper password.
		If you made the Vault with a Keyfile, make sure to pick the Keyfile.
4. This is the main screen of the application.
	Here's you can add entries to the Vault, change entries, or delete them.
		The top of the window 
	To add an entry:
		i) Type in a label. This label will be used to represent the entry in the list shown below.
		ii) Type in the account's username and password.

+-------------------------
| Referenced Websites:
+-------------------------
http://docs.oracle.com/javase/7/docs/technotes/guides/security/index.html
https://docs.oracle.com/javase/7/docs/api/overview-summary.html
	The javadocs explained how to use the KeyStore class.

http://stackoverflow.com/questions/18243248/java-keystore-setentry-using-an-aes-secretkey#comment26751703_18243248
	This answer showed me the proper Keystore type to use when making a KeyStore.
http://stackoverflow.com/questions/21250330/decryption-of-aes-encrypted-field-in-java#21250773
	This answer showed me how to debug the KeyStore's values.
	Instead of printing the array, I had to convert the array into a String first.

http://stackoverflow.com/questions/11758440/initialize-final-variable-within-constructor-in-another-method#11758483
	This answer served as inspiration for VaultEntry.reserveFreeId()
http://stackoverflow.com/a/27609
	This answer helped me write VaultEntry.equals(), which is used to display the VaultEntries in a JList.

https://docs.oracle.com/javase/tutorial/uiswing/components/list.html#creating
	The javadocs showed me how to integrate my Vault class with Java Swing.
https://docs.oracle.com/javase/7/docs/api/java/lang/ref/WeakReference.html
	I used the javadocs to figure out how to create a WeakReference to the VaultListModel.

http://stackoverflow.com/questions/6694715/junit-testing-private-variables
	This website showed me how to use Netbean's Unit Tests feature.
