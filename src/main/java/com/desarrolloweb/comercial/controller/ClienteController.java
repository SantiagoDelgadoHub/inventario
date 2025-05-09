package com.desarrolloweb.comercial.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.desarrolloweb.comercial.model.entity.Cliente;
import com.desarrolloweb.comercial.model.entity.Documento;
import com.desarrolloweb.comercial.model.service.ComercialServiceIface;
import com.desarrolloweb.comercial.utils.paginator.PageRender;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/comercial")
@SessionAttributes("cliente")
public class ClienteController {

	 private final Logger logger = LoggerFactory.getLogger(getClass());

	 // Se establece el formato de los decimales
	 DecimalFormat df = new DecimalFormat("###,##0.0");

	 // Formato de fecha
	 SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

	@Value("${uploads.path}")
	private String uploadsExtDir;

    private final ComercialServiceIface comercialService;

    public ClienteController(ComercialServiceIface comercialService) {
        this.comercialService = comercialService;
    }
    
	@GetMapping("/clienteslistar")
	public String clientesListar(@RequestParam (defaultValue = "0")int pag,Model model) {

		//List<Cliente> clientes = comercialService.buscarClientesTodos();

		Pageable pagina =  PageRequest.of(pag,5);
		Page <Cliente> clientes=comercialService.buscarTodosLosClientes(pagina);

		System.out.println("### resumen del page");
		System.out.println("Total paginas recuperadas"+clientes.getTotalPages());
		System.out.println("Total registros recuperadas"+clientes.getTotalElements());
		System.out.println("Pagina actual"+clientes.getNumber());
		System.out.println("Numero elementos por pagina"+clientes.getSize());
		System.out.println("Numero elementos de la pagina actual"+clientes.getNumberOfElements());
		System.out.println("hay una pagina anterior?"+clientes.hasPrevious());
		System.out.println("hay una pagina siguiente?"+clientes.hasNext());
		System.out.println("es la primera pagina?"+clientes.isFirst());
		System.out.println("es la ultima pagina?"+clientes.isLast());

		PageRender pageRender = new PageRender<>("/comercial/clienteslistar", clientes);

		model.addAttribute("pageRender", pageRender);
		model.addAttribute("titulo", "Listado de clientes activos");
		model.addAttribute("clientes", clientes);
		return "cliente/listado_clientes";
	}

	@GetMapping("/clientenuevo")
	public String clienteFormNuevo(Model model) {
		model.addAttribute("titulo", "Nuevo cliente");
		model.addAttribute("accion", "Crear");
		model.addAttribute("cliente", new Cliente());
		return "cliente/formulario_cliente";
	}

	@PostMapping("/clienteguardar")
	public String clienteGuardar(@Valid @ModelAttribute Cliente cliente, BindingResult result, 
			Model model, SessionStatus status, RedirectAttributes flash, @RequestParam(name = "file") MultipartFile imagen) {

		String accion = (cliente.getId() == null) ? "Guardar" : "Modificar";
		
		if (result.hasErrors()) {
			model.addAttribute("titulo", "Nuevo cliente");
			model.addAttribute("accion", accion);
			model.addAttribute("info", "Complemente o corrija la información de los campos del formulario");
			return "cliente/formulario_cliente";
		}

		if (!imagen.isEmpty()) {

			logger.info("######### Peso:" + df.format((double)imagen.getSize()/1024) + "KB");
			logger.info("Tipo :" + imagen.getContentType().substring(imagen.getContentType().indexOf("/")+1));

			if (cliente.getId() != null && cliente.getId() > 0 && 
				cliente.getImagen() != null && cliente.getImagen().length() > 0) {

				Path rutaAbsUploads = Paths.get(uploadsExtDir).resolve(cliente.getImagen()).toAbsolutePath();
				
				try {
					BasicFileAttributes attributes=Files.readAttributes(rutaAbsUploads,BasicFileAttributes.class);
					FileTime fechaCreacion=attributes.creationTime();
					FileTime fechaModificacion=attributes.lastModifiedTime();
					logger.info("#####  Creado en: " + sdf.format(new Date(fechaCreacion.toMillis())));
					logger.info("#####  Modificado  en: " + sdf.format(new Date(fechaModificacion.toMillis())));


				} catch (IOException e) {
					
					e.printStackTrace();
				}

				File archivo = rutaAbsUploads.toFile();
				if (archivo.exists() && archivo.canRead()) {
					archivo.delete();
				}
			}

			// String nombreArchivo = imagen.getOriginalFilename();		
			// Le asigna un nombre unico al archivo	
			// String nombreArchivo = UUID.randomUUID().toString() + "__" + imagen.getOriginalFilename();
			String nombreArchivo = UUID.randomUUID().toString().substring(0,8) + "__" + imagen.getOriginalFilename();


			Path rutaUploads = Paths.get(uploadsExtDir).resolve(nombreArchivo);
			Path rutaAbsUploads = rutaUploads.toAbsolutePath();

			try {
				Files.copy(imagen.getInputStream(), rutaAbsUploads);
				cliente.setImagen(nombreArchivo);
				flash.addFlashAttribute("info", "El archivo " + nombreArchivo + " fue cargado");
			} catch (Exception e) {
				System.out.println(e.toString());
			}
		}

		comercialService.guardarCliente(cliente);
		status.setComplete();
		flash.addFlashAttribute("success", "El registro fue " + (cliente.getId() == null ? "agregado" : "modificado") + " con éxito");
		return "redirect:/comercial/clienteslistar";
	}

	@GetMapping("/clienteconsultar/{id}")
	public String clienteConsultar(@PathVariable Long id, RedirectAttributes flash, Model model) {
		// Cliente cliente = comercialService.buscarClientePorId(id);
		Cliente cliente = comercialService.buscarClientePorIdConFactura(id);
		if (cliente == null) {
			flash.addFlashAttribute("warning", "El registro no fue hallado en la base de datos");
			return "redirect:/comercial/clienteslistar";
		}

		Documento clienteDocumentos=comercialService.buscarDocumentoPorCliente(id);
		model.addAttribute("documentos", clienteDocumentos);
		model.addAttribute("titulo", "Consulta del cliente: " + cliente.getNombreCompleto());
		model.addAttribute("cliente", cliente);
		//model.addAttribute("documentos", clienteDocumentos);
		return "cliente/consulta_cliente";
	}	

	@GetMapping("/clientemodificar/{id}")
	public String clienteFormModificar(@PathVariable Long id, RedirectAttributes flash, Model model) {
		Cliente cliente = null;
		if (id > 0) {
			cliente = comercialService.buscarClientePorId(id);
			if (cliente == null) {
				flash.addFlashAttribute("warning", "El registro no fue hallado en la base de datos");
				return "redirect:/comercial/clienteslistar";
			}
		}
		else {
			flash.addFlashAttribute("error", "Error, el ID no es válido !!");
			return "redirect:/comercial/clienteslistar";
		}
		model.addAttribute("accion", "Modificar");
		model.addAttribute("titulo", "Modificar cliente");
		model.addAttribute("cliente", cliente);
		return "cliente/formulario_cliente";
	}
	
	@GetMapping("/clienteeliminar/{id}")
	public String clienteEliminar(@PathVariable Long id, RedirectAttributes flash) {
		if (id > 0) {
			Cliente cliente = comercialService.buscarClientePorId(id);
			if(cliente != null) {
				comercialService.eliminarClientePorId(id);
				flash.addFlashAttribute("success", "El registro fue eliminado de la base de datos");
				Path rutaAbsUploads = Paths.get(uploadsExtDir).resolve(cliente.getImagen());
				File archivo = rutaAbsUploads.toFile();
				archivo.delete();
			}
		}
		else {
			flash.addFlashAttribute("error", "Error, el ID no es válido !!");
		}
		return "redirect:/comercial/clienteslistar";
	}	
} 
