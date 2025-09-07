package Lab5.common.src;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Вспомогательный класс для демаршелинга
 */
@XmlRootElement(name = "LabWorks")
@XmlAccessorType(XmlAccessType.FIELD)
public class LabWorkList {

    @XmlElement(name = "LabWork")
    private List<LabWork> labWorks = new ArrayList<>();

    public LabWorkList() {}

    public LabWorkList(Collection<LabWork> collection) {
        this.labWorks = new ArrayList<>(collection);
    }

    public List<LabWork> getLabWorks() {
        return labWorks;
    }
}
