package com.bolsadeideas.springboot.form.app.controllers;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//import java.util.HashMap;
//import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.SessionAttribute;
//import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;

import com.bolsadeideas.springboot.form.app.editors.NombreMayusculaEditor;
import com.bolsadeideas.springboot.form.app.editors.PaisPropertyEditor;
import com.bolsadeideas.springboot.form.app.editors.RolesEditor;
import com.bolsadeideas.springboot.form.app.models.domain.Pais;
import com.bolsadeideas.springboot.form.app.models.domain.Role;
import com.bolsadeideas.springboot.form.app.models.domain.Usuario;
import com.bolsadeideas.springboot.form.app.services.PaisService;
import com.bolsadeideas.springboot.form.app.services.RoleService;
import com.bolsadeideas.springboot.form.app.validation.UsuarioValidador;

@Controller
@SessionAttributes("usuario")
public class FormController {
	
	@Autowired
	private UsuarioValidador validador;
	
	@Autowired
	private PaisService paisService;
	@Autowired
	private PaisPropertyEditor paisEditor;
	
	@Autowired
	private RoleService roleService;
	@Autowired
	private RolesEditor roleEditor;
	
	@InitBinder
	public void initBinder(WebDataBinder binder) {
		binder.addValidators(validador);
		
		//Formatear la fecha
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		dateFormat.setLenient(false);
		binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, false));
		
		//Nombre/Apellido en mayusculas
		binder.registerCustomEditor(String.class, "nombre", new NombreMayusculaEditor());
		
		//Id/ codigo de pais aplicado al campo select 
		binder.registerCustomEditor(Pais.class, "pais", paisEditor);
		
		//Id/ Editor especifico de los Checkboxes
		binder.registerCustomEditor(Role.class, "roles", roleEditor);
	}
	
	@ModelAttribute("genero")
	public List<String> genero(){
		return Arrays.asList("Hombre", "Mujer");
	}
	
	@ModelAttribute("listaRoles")
	public List<Role> listaRoles(){
		return this.roleService.listar();
	}
	
	@ModelAttribute("listaRolesString")
	public List<String>listaRolesString(){
		List<String> roles = new ArrayList<>();
		
		roles.add("ROLE_ADMIN");
		roles.add("ROLE_USER");
		roles.add("ROLE_MODERADOR");
		
		return roles;
	}
	
	@ModelAttribute("listaRolesMap")
	public Map<String, String> listaRolesMpa(){
		
		Map<String, String> roles = new HashMap<String, String>();
		
		roles.put("ROLE_ADMIN", "Administrador");
		roles.put("ROLE_USER", "Usuario");
		roles.put("ROLE_MODERADOR", "Moderador");
		
		return roles;
	}
	
	@ModelAttribute("listaPaises")
	public List<Pais> listaPaises(){
		return paisService.listar();
	}
	
	@ModelAttribute("paises")
	public List<String> paises(){
		return Arrays.asList("España", "México", "Chile", "Perú", "Colombia", "Venezuela");
	}
	
	@ModelAttribute("paisesMapa")
	public Map<String, String> paisesMapa(){
		
		Map<String, String> paises = new HashMap<String, String>();
		
		paises.put("ES", "España");
		paises.put("MX", "México");
		paises.put("CL", "Chile");
		paises.put("PE", "Perú");
		paises.put("CO", "Colombia");
		paises.put("VE", "Venezuela");
		
		return paises;
	}
	
	 @GetMapping("/form")
	 public String form(Model model) {
		 Usuario usuario = new Usuario();
		 
		 usuario.setNombre("John");
		 usuario.setApellido("Doe");
		 usuario.setIdentificador("12.456.789-K");
		 usuario.setHabilitar(true);
		 usuario.setValorSecreto("Algun Valor secreto");
		 usuario.setPais(new Pais(2, "MX", "México"));
		 usuario.setRoles( Arrays.asList(new Role(2, "Usuario", "ROLE_USER")) );
		 
		 model.addAttribute("titulo", "Formulario Usuarios");
		 model.addAttribute("usuario", usuario);
		return "form";
	 }
	
	/*
	@PostMapping("/form")
	public String procesar(Model model, 
			 				@RequestParam String username,
			 				@RequestParam String password, 
			 				@RequestParam String email) 
	{
		//Instancia de Usuario
		Usuario usuario = new Usuario();
		usuario.setUsername(username);
		usuario.setPassword(email);
		usuario.setEmail(email);
		
		model.addAttribute("titulo", "Resultados del Formulario");
		model.addAttribute("usuario", usuario);
		
		return "resultado";
	}*/
	 
	/* Mapeando el modelo con los campos del formulario */
	
	@PostMapping("/form")
	public String procesar(@Valid Usuario usuario, BindingResult result, Model model/*, SessionStatus status*/) {
		
		//validador.validate(usuario, result);
		
		//model.addAttribute("titulo", "Resultados del Formulario");
		
		//Si hay errores al llenar los campos 
		if(result.hasErrors()) {
			/*
			//Creamos un mapa de datos
			Map<String, String> errores = new HashMap<>();
			
			//Insertamos los errores en el mapa
			result.getFieldErrors().forEach( err -> {
				errores.put(err.getField(), "El campo ".concat(err.getField()).concat(" ").concat(err.getDefaultMessage()) );
			});
			
			model.addAttribute("error", errores);
			*/
			model.addAttribute("titulo", "Resultados del Formulario");
			return "form";
		}
		
		//model.addAttribute("usuario", usuario);
		//status.setComplete();
		return "redirect:/ver";
	}
	
	@GetMapping("/ver")
	public String ver(@SessionAttribute(name= "usuario", required = false) Usuario usuario,  Model model, SessionStatus status ) {
		
		if(usuario == null) {
			return "redirect:/form";
		}
		
		model.addAttribute("titulo", "Resultados del Formulario");
		status.setComplete();
		return "resultado";
	}
	 

}
