package model;

import java.util.ArrayList;
import java.util.List;

public class OrganizationNode {
    private int id;
    private String name;
    private String type;
    private String title;
    private String avatarUrl;
    private List<OrganizationNode> children = new ArrayList<>();

    public OrganizationNode() {}

    public OrganizationNode(int id, String name, String type) {
        this.id = id;
        this.name = name;
        this.type = type;
    }

    // getters & setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getAvatarUrl() { return avatarUrl; }
    public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }
    public List<OrganizationNode> getChildren() { return children; }
    public void setChildren(List<OrganizationNode> children) { this.children = children; }
    public void addChild(OrganizationNode child) { this.children.add(child); }
}