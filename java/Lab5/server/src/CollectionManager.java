package Lab5.server.src;

import Lab5.common.src.LabWork;

import java.util.*;

// TODO автоматическая подгрузка даты и айди
// TODO айди доработать (сейчас генерация лонга)

/**
 * Хранение и управление коллекцией
 */
public class CollectionManager {
    private Set<LabWork> labWorks = new HashSet<>();
    private final Date currentDate;


    public CollectionManager() {
        this.currentDate = new Date();
    }

    /**
     * Обновляет внутренние данные, очищая старые и добавляя новые.
     *
     * @param data новые объекты LabWork
     */
    public void setInitialData(Collection<LabWork> data) {
        labWorks.clear();
        labWorks.addAll(data);
    }

    /**
     * Возвращает копию всех элементов коллекции.
     */
    public Set<LabWork> getLabWorks() {
        return new HashSet<>(labWorks);
    }

    /**
     * Интерактивно строит новый LabWork и добавляет его в коллекцию.
     *
     * @return только что добавленный объект
     */
    public LabWork add(LabWork labWork) {
//        LabWork lw = inputManager.buildLabWork();
        labWorks.add(labWork);
        return labWork;
    }

    public int removeAll(Collection<LabWork> toRemove) {
        int before = labWorks.size();
        labWorks.removeAll(toRemove);
        return before - labWorks.size();
    }

    @Deprecated
    public void removeLabWork(LabWork oldElement) {
        labWorks.remove(oldElement);
    }

    public int size() {
        return labWorks.size();
    }

    public void replace(LabWork old, LabWork newE) {
        labWorks.remove(old);
        labWorks.add(newE);
    }

    /**
     * Возвращает тип внутренней коллекции, например \"HashSet\"
     */
    public String getCollectionType() {
        return labWorks.getClass().getSimpleName();
    }

    /**
     * Возвращает копию даты/времени инициализации менеджера
     */
    public Date getDate() {
        return new Date(currentDate.getTime());
    }

    /**
     * Хэшсет не гарантирует сортировку, поэтому используем дерево.
     *
     * @return ArrayList для удобства вывода
     */
    public List<LabWork> getSorted() {
        TreeSet<LabWork> sortedSet = new TreeSet<>(labWorks);
        return new ArrayList<>(sortedSet);
    }

    public void clear() {
        labWorks.clear();
    }

    public boolean removeById(long id) {
        return labWorks.removeIf(lw -> lw.getId() == id);
    }

    /**
     * Ищет первый LabWork с данным id.
     *
     * @return найденный объект или null, если нет
     */
    @Deprecated
    public LabWork getById(long id) {
        for (LabWork lw : labWorks) {
            if (lw.getId() == id) {
                return lw;
            }
        }
        return null;
    }

    /**
     * Заменяет в коллекции LabWork с тем же ID на новый объект.
     *
     * @param updated полностью заполненный LabWork (должен содержать старый id)
     * @return true, если замена произошла; false, если элемент с таким ID не найден
     */
    public boolean replaceById(LabWork updated) {
        if (updated == null) return false;
        long id = updated.getId();               // берём старый id
        Optional<LabWork> oldOpt = labWorks.stream()
                .filter(lw -> lw.getId() == id)
                .findFirst();
        if (oldOpt.isEmpty()) return false;
        LabWork old = oldOpt.get();

        // Снимаем старый объект и вставляем вместо него новый
        labWorks.remove(old);
        labWorks.add(updated);

        return true;
    }

    /**
     * Ищет первый LabWork с данным id.
     *
     * @return true - если коллекция пустая, false - если нет
     * @see Boolean
     */
    public boolean isEmpty() {
        return labWorks.isEmpty();
    }


//    class SortByName implements Comparator<LabWork> {
//        @Override
//        public int compare(LabWork o1, LabWork o2) {
//            return o1.getName().compareTo(o2.getName());
//        }
//    }
//
//    class SortById implements Comparable<LabWork>{
//        @Override
//        public int compareTo(LabWork o) {
//            return Labwork.id == o.getId();
//        }
//        }
//    }
// сортировку прописать в классах команд


    //    addLabWork(LabWork labWork) – добавляет объект.
//    getSortedById() – сортировка по ID.
//            getSortedByName() – сортировка по имени.
//            getSortedByCoordinates() – сортировка по координатам.
//            getSortedByMinimalPoint() – сортировка по минимальной оценке.
}
