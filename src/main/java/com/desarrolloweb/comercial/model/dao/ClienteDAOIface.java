package com.desarrolloweb.comercial.model.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.desarrolloweb.comercial.model.entity.Cliente;

public interface ClienteDAOIface extends JpaRepository<Cliente, Long>{

    @Query("select c from Cliente c left join fetch c.facturas f where c.id = ?1")
    public Cliente buscarPorIdConFacturas(Long id);
}
