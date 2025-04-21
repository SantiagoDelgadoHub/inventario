package com.desarrolloweb.comercial.model.dao;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.desarrolloweb.comercial.model.entity.Documento;


public interface DocumetoDAOIface extends JpaRepository<Documento,Long>{

    @Query("SELECT d FROM Documento d JOIN FETCH d.cliente WHERE d.id = ?1")
    public Documento buscarDocumentoPorCliente(Long id);


}

    

    

