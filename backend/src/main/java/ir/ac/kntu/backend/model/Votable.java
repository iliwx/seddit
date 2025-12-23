package ir.ac.kntu.backend.model;

public interface Votable {
    Long getId();
    long getVotes();
    void setVotes(long votes);
}