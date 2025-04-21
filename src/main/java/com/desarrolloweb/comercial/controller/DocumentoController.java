package com.desarrolloweb.comercial.controller;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.desarrolloweb.comercial.model.entity.Cliente;
import com.desarrolloweb.comercial.model.entity.Documento;
import com.desarrolloweb.comercial.model.service.ComercialServiceIface;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequestMapping("/comercial")
// @SessionAttributes("cliente")
public class DocumentoController {

    DecimalFormat df = new DecimalFormat("###,##0.0");
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    @Value("${uploads.path}")
	private String uploadsExtDir;

    private final ComercialServiceIface comercialService;

    public DocumentoController(ComercialServiceIface comercialService) {
        this.comercialService = comercialService;
    }

    @GetMapping("/crearDocumentos")
    public String crearDocumentos(@RequestParam(name = "id") Long id, Model model) {

        Cliente cliente = comercialService.buscarClientePorId(id);

        model.addAttribute("cliente", cliente);
        model.addAttribute("titulo", "Crear documento para: ");
        model.addAttribute("accion", "Crear");
        model.addAttribute("documento", new Documento());

        return "cliente/formulario_documento";
    }

    @PostMapping("/documentosGuardar")
    public String guardarDocumentos(@Valid @ModelAttribute Documento documento,
            @RequestParam("clienteId") Long clienteId, Model model,
            SessionStatus status, RedirectAttributes flash, @RequestParam(name = "file") MultipartFile file) {

        Cliente cliente = comercialService.buscarClientePorId(clienteId);

        if (!file.isEmpty()) {

            documento.setCliente(cliente);

            String nombreArchivo =  file.getOriginalFilename();
            Path rutaUploads = Paths.get("uploads").resolve(nombreArchivo);
            Path rutaAbsUploads = rutaUploads.toAbsolutePath();

            try {
                Files.copy(file.getInputStream(), rutaAbsUploads);
                documento.setNombreDocumento(nombreArchivo);
                flash.addFlashAttribute("info", "El documento " + nombreArchivo + " fue cargado correctamente");
            } catch (Exception e) {
                System.out.println("Error al subir el archivo: " + e.toString());
                flash.addFlashAttribute("error", "Error al subir el archivo");
                return "redirect:/comercial/documentosForm";
            }

            documento.setTamanio(df.format((double) file.getSize() / 1024) + "KB");
            documento.setTipoArchivo(file.getContentType().substring(file.getContentType().indexOf("/") + 1));
            documento.setFechaCarga(LocalDateTime.now());
            documento.setFechaCreacion(LocalDateTime.now());
            comercialService.guardarDocumento(documento);
        }
        return "redirect:/comercial/clienteconsultar/" + clienteId;
    }

    @GetMapping("/documentoEliminar/{id}/{clienteId}")
    public String documentoEliminar(@PathVariable Long id,@PathVariable Long clienteId,
                                    RedirectAttributes flash) {
        if(id > 0){
            Documento documento = comercialService.buscarDocumentoPorId(id);
            if(documento != null){
                comercialService.eliminarDocumentoPorId(id);
                flash.addFlashAttribute("success", "El registro fue eliminado de la base de datos");
    
                Path rutaAbsUploads = Paths.get(uploadsExtDir).resolve(documento.getNombreDocumento());
                File archivo = rutaAbsUploads.toFile();
                archivo.delete();
            } else {
                flash.addFlashAttribute("error", "Error, el ID no es válido !!");
            }
        }
        return "redirect:/comercial/clienteconsultar/" + clienteId;
    }
    

  @GetMapping("/documentoDetallado/{id}")
  public String documentoDetallado(@PathVariable Long id, Model model) {
        
    Documento documento=comercialService.buscarDocumentoPorId(id);

    model.addAttribute("documento", documento);
    model.addAttribute("titulo", "Documento detallado");

      return "cliente/documeto_detallado" ;
  }
  }
