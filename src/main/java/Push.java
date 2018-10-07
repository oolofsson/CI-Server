import java.util.List;

public class Push {

    /**
     * Class for storing the JSON objects sent from Github
     *
     *
     */
    private String before;
    private String after;
    private List<Commit> commits;
    private String compare_link;
    private Commit head_commit;
    private Repository repository;

    /**
     * Returns list of all commits made in push
     * @return List<Commit>
     */
    public List<Commit> getCommits() {
        return commits;
    }

    /**
     * Returns compare link of push
     * @return String
     */
    public String getCompare_link() {
        return compare_link;
    }

    /**
     * Returns commit id of head_commit (latest commit)
     * @return Commit object
     */
    public Commit getHead_commit() {
        return head_commit;
    }

    /**
     * Returns commit id before push
     * @return String
     */
    public String getBefore() {
        return before;
    }

    /**
     * Returns commit id after push
     * @return String
     */
    public String getAfter() {
        return after;
    }

    /**
     * Returns repository info
     * @return Repository Object
     */
    public Repository getRepository() {
        return repository;
    }
}

class Commit{
    private String id;
    private String message;
    private String timestamp;
    private String url;
    private Author committer;
    private List<String> modified;

    /**
     * Returns url to commit
     * @return String
     */
    public String getUrl() {
        return url;
    }
    /**
     * Returns id of commit
     * @return String
     */
    public String getId() {
        return id;
    }

    /**
     * Returns message of commit
     * @return String
     */
    public String getMessage() {
        return message;
    }

    /**
     * Returns timestamp of commit
     * @return String
     */
    public String getTimestamp() {
        return timestamp;
    }

    /**
     * Returns committer
     * @return Author object
     */
    public Author getComitter() {
        return committer;
    }

    /**
     * Returns list of modified file names
     * @return List<String>
     */
    public List<String> getModified() {
        return modified;
    }



}
/*
    Represents Author in push_payload
 */
class Author{
    private String name;
    private String email;
    /**
     * Returns name of committer
     * @return String
     */
    public String getName() {
        return name;
    }

    /**
     * Returns email of committer
     * @return String
     */
    public String getEmail() {
        return email;
    }
}
/*
    Represents Repository in push_payload
 */
class Repository{
    private int id;
    private  String ssh_url;
    /**
     *     Returns repository id
     *     @return int
     */
    public int getId() {
        return id;
    }
    /**
     * Returns git url of repository
     * @return String
     */
    public String getSsh_url() {
        return ssh_url;
    }
}