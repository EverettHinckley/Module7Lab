package aydin.firebasedemospring2024;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutionException;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.firestore.WriteResult;
import com.google.firebase.auth.ExportedUserRecord;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.ListUsersPage;
import com.google.firebase.auth.UserRecord;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class SecondaryController {

    @FXML
    private Button registerButton;
    @FXML
    private TextField emailTextField;
    @FXML
    private PasswordField passwordTextField;
    @FXML
    private Label welcome;
    @FXML
    void registerButtonClicked(ActionEvent event) {
        registerUser();
    }
    @FXML
    private void switchToPrimary() throws IOException, FirebaseAuthException {
        ListUsersPage page = DemoApp.fauth.listUsers(null);
        ApiFuture<QuerySnapshot> future=DemoApp.fstore.collection("Users").get();
        List<QueryDocumentSnapshot> documents;
        boolean userFound=false;
        try {
            documents=future.get().getDocuments();
            int docNum=0;
            for (ExportedUserRecord user : page.iterateAll()) {
                if (user.getUid().equals(documents.get(docNum).getData().get("userID"))&&passwordTextField.getText().equals(documents.get(docNum).getData().get("password"))) {
                    System.out.println("User signed in");
                    DemoApp.setRoot("primary");
                    userFound=true;
                }
                docNum++;
            }
        }
        catch(InterruptedException | ExecutionException ex){}
        if(!userFound){ System.out.println("User not yet in system");}
    }
    public void addData(String id) {

        DocumentReference docRef = DemoApp.fstore.collection("Users").document(UUID.randomUUID().toString());

        Map<String, Object> data = new HashMap<>();
        data.put("userID", id);
        data.put("password", passwordTextField.getText());

        //asynchronously write data
        ApiFuture<WriteResult> result = docRef.set(data);
    }
    public boolean registerUser() {
        UserRecord.CreateRequest request = new UserRecord.CreateRequest()
                .setEmail(emailTextField.getText())
                .setEmailVerified(false)
                .setPassword(passwordTextField.getText())
                .setPhoneNumber("+11234567890")
                .setDisplayName("John Doe")
                .setDisabled(false);

        try {
            UserRecord userRecord = DemoApp.fauth.createUser(request);
            addData(userRecord.getUid());
            System.out.println("Successfully created new user with Firebase Uid: " + userRecord.getUid()
                    + " check Firebase > Authentication > Users tab");
            return true;

        } catch (FirebaseAuthException ex) {
            // Logger.getLogger(FirestoreContext.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("Error creating a new user in the firebase");
            return false;
        }

    }
}
