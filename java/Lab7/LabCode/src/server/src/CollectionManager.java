package src.server.src;


import src.common.src.LabWork;

import java.util.*;
import java.util.stream.Collectors;

public class CollectionManager {
    private final Set<LabWork> labWorks = Collections.synchronizedSet(new HashSet<>());
    private final Date initDate = new Date();

    public CollectionManager() {}
    public CollectionManager(Collection<LabWork> initial) {
        if (initial != null) labWorks.addAll(initial);
    }

    public Set<LabWork> snapshot() {
        synchronized (labWorks) { return new HashSet<>(labWorks); }
    }

    public List<LabWork> getSorted() {
        synchronized (labWorks) {
            return labWorks.stream().sorted().collect(Collectors.toList());
        }
    }

    public void add(LabWork lw) { labWorks.add(lw); }

    public boolean removeById(long id) {
        synchronized (labWorks) { return labWorks.removeIf(lw -> lw.getId() == id); }
    }

    public Optional<LabWork> findById(long id) {
        synchronized (labWorks) {
            return labWorks.stream().filter(lw -> lw.getId() == id).findFirst();
        }
    }

    public boolean replaceById(LabWork replacement) {
        synchronized (labWorks) {
            return findById(replacement.getId()).map(old -> {
                replacement.setOwnerLogin(old.getOwnerLogin());
                replacement.setCreationDate(old.getCreationDate());
                labWorks.remove(old);
                labWorks.add(replacement);
                return true;
            }).orElse(false);
        }
    }

    public int removeAll(Collection<LabWork> toRemove) {
        synchronized (labWorks) {
            int before = labWorks.size();
            labWorks.removeAll(toRemove);
            return before - labWorks.size();
        }
    }

    public int size() { return labWorks.size(); }
    public Date getInitDate() { return new Date(initDate.getTime()); }

    public String getCollectionType() {
        synchronized (labWorks) {
            return HashSet.class.getSimpleName();
        }
    }

    public java.util.Set<LabWork> getLabWorks() {
        synchronized (labWorks) {
            return new java.util.HashSet<>(labWorks);
        }
    }
}
