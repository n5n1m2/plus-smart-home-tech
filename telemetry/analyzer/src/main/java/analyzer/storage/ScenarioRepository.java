package analyzer.storage;

import analyzer.model.Scenario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ScenarioRepository extends JpaRepository<Scenario, Long> {
    void deleteByHubIdAndName(String hubId, String name);

    List<Scenario> findByHubId(String hubId);

    Optional<Scenario> findByHubIdAndName(String hubId, String name);

    @Query("SELECT s FROM Scenario s LEFT JOIN FETCH s.conditions WHERE s.hubId = :hubId")
    List<Scenario> findByHubIdWithConditions(@Param("hubId") String hubId);

}
