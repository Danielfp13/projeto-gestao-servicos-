package com.api.clientes.repository;

import com.api.clientes.model.entity.ServicoPrestado;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ServicoPrestadoRepository extends JpaRepository<ServicoPrestado,Integer> {

     @Query("select s from ServicoPrestado s join s.cliente c where upper(c.nome) like upper(:nome)" +
             "and concat (0, cast( extract( month from s.data) as text)) like :mes")
   /* @Query(value = "select s.*" +
            "from servico_prestado s \n" +
            "inner join cliente c on c.id = s.id_cliente\n" +
            "where upper (c.nome) ilike upper(:nome)" +
            "and concat( 0, cast( extract( month from s.data) as text ) ) like :mes", nativeQuery = true)*/
     Page<ServicoPrestado> findByNameClienteAndMonth(@Param("nome") String nome, @Param("mes")String mes, Pageable pageable);
}
