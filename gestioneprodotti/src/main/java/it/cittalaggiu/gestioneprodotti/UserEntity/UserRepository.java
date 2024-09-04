package it.cittalaggiu.gestioneprodotti.UserEntity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity,Long>, PagingAndSortingRepository<UserEntity,Long> {


    Optional<UserEntity> findOneByUsername(String username);

    boolean existsByUsername(String username);

    List<UserEntity> findByUsernameStartingWithIgnoreCase(String prefix);


}
