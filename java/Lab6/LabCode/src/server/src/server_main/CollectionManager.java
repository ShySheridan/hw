package server_main;

import common_main.LabWork;

import java.util.*;
import java.util.stream.Collectors;

public class CollectionManager {
    private final Set<LabWork> labWorks = new HashSet<>();
    private final Date currentDate = new Date();
    private long nextId = 1L;

    public CollectionManager() {
    }

    public CollectionManager(Collection<LabWork> initial) {
        if (initial != null) labWorks.addAll(initial);
    }

    public Set<LabWork> getLabWorks() {
        return new HashSet<>(labWorks);
    }

    public LabWork add(LabWork lw) {
        labWorks.add(lw);
        return lw;
    }

    public int removeAll(Collection<LabWork> toRemove) {
        int before = labWorks.size();
        labWorks.removeAll(toRemove);
        return before - labWorks.size();
    }

    public boolean removeById(long id) {
        return labWorks.removeIf(lw -> lw.getId() == id);
    }

    public void clear() {
        labWorks.clear();
    }

    public List<LabWork> getSorted() {
        return labWorks.stream()
                .sorted() // натуральный порядок (Comparable по name)
                .collect(Collectors.toList());
    }

    public int size() {
        return labWorks.size();
    }

    public boolean isEmpty() {
        return labWorks.isEmpty();
    }

    public String getCollectionType() {
        return labWorks.getClass().getSimpleName();
    }

    public Date getDate() {
        return new Date(currentDate.getTime());
    }

    public void addWithAutoFields(LabWork lw) {
        Objects.requireNonNull(lw, "lw");
        lw.setId(nextId++);
        if (lw.getCreationDate() == null) {
            lw.setCreationDate(new Date());
        }
        labWorks.add(lw);
    }

    public Optional<LabWork> findById(long id) {
        return labWorks.stream().filter(lw -> lw.getId() == id).findFirst();
    }

    public boolean replaceById(LabWork replacement) {
        if (replacement == null) return false;
        long id = replacement.getId();
        var opt = findById(id);
        if (opt.isEmpty()) return false;

        var old = opt.get();
        replacement.setId(old.getId());
        replacement.setCreationDate(old.getCreationDate());

        labWorks.remove(old);
        labWorks.add(replacement);
        return true;
    }
}
