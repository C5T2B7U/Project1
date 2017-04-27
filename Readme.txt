+--------------------------------------------------+
| LoginSecrets: Secure Password Vault
+--------------------------------------------------+
LoginSecrets is a portable, cross-platform solution for securely storing lists of account login credentials.  Users can create new or open existing encrypted password vaults, then view, add, update, and delete entries before saving and closing the password vaults.  Vault files can then be safely transferred over non-secure mediums.
By default, password vaults rely on both a password and a keyfile for (optional) two-factor authentication to access the encrypted data stored within.

You can trust LoginSecrets to keep your login details secret*.

*NOTE:  Due to platform limitations, for maximum protection it is highly recommended to reboot the computer after using LoginSecrets!

LoginSecrets was developed by Brian Sumner, Kevin Yang, and John Crosby of UCDenver.  Copyright 2017.

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
		The bottom of the window shows all the entries in the Vault.
		When you select an entry, the text fields at the top show you the username & password in that entry.
			These text fields are also used to add & change entries.
	To add an entry:
		i) Type in a label. This label will be used to represent the entry in the Vault list.
		ii) Type in the account's username and password.
		iii) Click the "Add Entry" button.
	To change an entry:
		i) Select the entry you want to change.
		ii) Change the text fields at the top of the window.
		iii) Click "Update Entry"...
		iv) The button will change to "Really Update Entry?"
			Click it again to confirm the changes.
	To delete an entry:
		i) Select the entry you want to delete.
		ii) Click "Delete Entry"...
		iii) The button will change to "Really Delete Entry?"
			Click it again to confirm the deletion.
5. When you're done with the vault, click "Save Vault"
	The button will change to "Really Save Vault?"
	Click it again to confirm.
6. Finally, close the program.
	WARNING: If you close the Vault/program without saving the Vault, you WILL lose any changes to the Vault you applied.
	If you want to open another Vault:
		i) Click "Close Vault"
		ii) The button will change to "Really Close Vault?"
		iii) Click it again to confirm.


+-------------------------
| Examples:
+-------------------------
Some sample Vaults have been included with this program.
	vault1.st
		password = Vault1pass
		No Keyfile
	vault2.st
		password = 2Vaulted
		Keyfile = Lock.png

Note: The logins in these vaults are completely made up.
	Any similarity to real user credentials is completely coincidential.


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

http://stackoverflow.com/questions/3213045/centering-text-in-a-jtextarea-or-jtextpane-horizontal-text-alignment#3213361
https://docs.oracle.com/javase/tutorial/uiswing/components/filechooser.html
http://www.rgagnon.com/javadetails/java-0370.html
	These websites helped us design the user interface.

https://commons.wikimedia.org/wiki/File:Lock_PD.png
	This is the image (Keyfile) we used to generate vault2.st
	It's in the public domain. "The right to use this work is granted to anyone for any purpose, without any conditions, unless such conditions are required by law."
