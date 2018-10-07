import com.google.gson.Gson;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import static org.junit.Assert.*;

public class PushTest {
    @Test
    public void test(){
        //Contract: Push should describe correct fields to take from webhook request using gson deserializer
        // and fields should have correct values parsed.
        Gson gson = new Gson();

        try {

            BufferedReader reader = new BufferedReader(new FileReader("src/test/resources/json_push_payload_example.txt"));
            Push push_payload = gson.fromJson(reader, Push.class);

            //before commit id
            assertEquals("ecdfe4a5320376fb33310adcc01febebd31e19de", push_payload.getBefore());
            //after commit id
            assertEquals("d36034986fe2a7d27cc3cafc4ca57455a9be928e", push_payload.getAfter());
            //should consist of two commits
            assertEquals(2, push_payload.getCommits().size());

            //head_commit: id
            assertEquals("d36034986fe2a7d27cc3cafc4ca57455a9be928e", push_payload.getHead_commit().getId());
            //head_commit: message
            assertEquals("second commit", push_payload.getHead_commit().getMessage());
            //head_commit: timestamp
            assertEquals("2018-01-31T21:01:13+01:00", push_payload.getHead_commit().getTimestamp());
            //head_commit: url
            assertEquals("https://gits-15.sys.kth.se/ziadsp/test-repository/commit/d36034986fe2a7d27cc3cafc4ca57455a9be928e", push_payload.getHead_commit().getUrl());
            //head_commit: Author name
            assertEquals("Ziad", push_payload.getHead_commit().getComitter().getName());
            //head_commit: Author email
            assertEquals("ziadgiliana@gmail.com", push_payload.getHead_commit().getComitter().getEmail());
            //head_commit: Modified fields
            assertEquals("README.md", push_payload.getHead_commit().getModified().get(0));




            //first commit: id
            assertEquals("acaf367f3a663a178fc66bb1300bfeb4d87770d8", push_payload.getCommits().get(0).getId());
            //first commit: message
            assertEquals("first commit", push_payload.getCommits().get(0).getMessage());
            //first commit: timestamp
            assertEquals("2018-01-31T21:01:00+01:00", push_payload.getCommits().get(0).getTimestamp());
            //first commit: url
            assertEquals("https://gits-15.sys.kth.se/ziadsp/test-repository/commit/acaf367f3a663a178fc66bb1300bfeb4d87770d8",push_payload.getCommits().get(0).getUrl());
            //first commit: author name
            assertEquals("Ziad", push_payload.getCommits().get(0).getComitter().getName());
            //first commit: author email
            assertEquals("ziadgiliana@gmail.com", push_payload.getCommits().get(0).getComitter().getEmail());
            //first commit: modified fileds
            assertEquals("README.md", push_payload.getCommits().get(0).getModified().get(0));


            //Second commit: id
            assertEquals("d36034986fe2a7d27cc3cafc4ca57455a9be928e", push_payload.getCommits().get(1).getId());
            //Second commit: message
            assertEquals("second commit", push_payload.getCommits().get(1).getMessage());
            //Second commit: timestamp
            assertEquals("2018-01-31T21:01:13+01:00", push_payload.getCommits().get(1).getTimestamp());
            //Second commit: url
            assertEquals("https://gits-15.sys.kth.se/ziadsp/test-repository/commit/d36034986fe2a7d27cc3cafc4ca57455a9be928e",push_payload.getCommits().get(1).getUrl());
            //Second commit: author name
            assertEquals("Ziad", push_payload.getCommits().get(1).getComitter().getName());
            //Second commit: author email
            assertEquals("ziadgiliana@gmail.com", push_payload.getCommits().get(1).getComitter().getEmail());
            //Second commit: modified fields
            assertEquals("README.md", push_payload.getCommits().get(0).getModified().get(0));


        }catch(IOException e){
            e.printStackTrace();
        }
    }

}
