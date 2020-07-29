package com.example.Controller;


import com.example.Dao.AuthorityDao;
import com.example.Dao.RoleDao;
import com.example.Dao.UserDao;
import com.example.Entity.Authority;
import com.example.Entity.Role;
import com.example.Entity.User;
import com.example.Service.EntityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;


import javax.websocket.server.PathParam;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Controller


public class EntityController {
    @Autowired
    private UserDao userRepository;
    @Autowired
    private RoleDao roleRepository;
    @Autowired
    private AuthorityDao authorityRepository;
    @Autowired
    private EntityService entityService;

    /*
        用户部分的增删查改
     */
    @RequestMapping("/finduser")
    @ResponseBody
    public List<User> findByName    (@RequestParam(value = "userName") String userName) {
        return userRepository.findAllByUserName(userName);
    }

    @PostMapping("findalluser/{userId}")
    @ResponseBody
    public List<User> findById(@PathVariable("userId") Integer userId) {
        return userRepository.findAllByUserId(userId);
    }
    @PostMapping("findallusers/{userName}")
    @ResponseBody
    public List<User> findByUserName(@PathVariable("userName") String userName) {
        return userRepository.findAllByUserName(userName);
    }
    @RequestMapping("/adduser")
    public String addUser(@RequestParam(value = "userName") String userName,
                              @RequestParam(value = "roleName") String roleName
                              ) {
        User user = new User();
        Role role = roleRepository.findAllByRoleName(roleName).get(0);
        user.setUserName(userName);
        user.setRoles(new ArrayList<>());
        user.getRoles().add(role);//给用户设置权限
        userRepository.save(user);
        return "redirect:/addUserToRole";
    }

    @GetMapping("addrole/{userName}")
    public ModelAndView findByName(@PathVariable("userName") String userName, Model model) {
        model.addAttribute("quanxian",userRepository.findAllByUserName(userName));
        return new ModelAndView("/addrole","model",model);
    }

    @RequestMapping("/adduserrole")
    @ResponseBody
    public List<User> addUserRole(@RequestParam(value = "userName") String userName,
                                  @RequestParam(value = "roleName") String roleName) {
        User user = userRepository.findAllByUserName(userName).get(0);
        Role role = roleRepository.findAllByRoleName(roleName).get(0);
        if (user.getRoles() == null) {
            user.setRoles(new ArrayList<>());
        }
        user.getRoles().add(role);//给用户设置权限
        userRepository.save(user);
        return userRepository.findAll();
    }

    @GetMapping("/deleteByUserName/{userName}")
    public ModelAndView deleteByUserName(@PathVariable("userName") String userName){
        System.out.println("username="+userName);
        entityService.deleteByUserName(userName);
        return  new ModelAndView("redirect:/quanxian");
    }

    /*
        查询用户权限
     */
    @RequestMapping("/getauth")
    @ResponseBody
    public Set<Authority> getAuthority(
            @RequestParam(value = "userName") String userName) {
        Set<Authority> authoritieSet = new HashSet<>();
        User user = userRepository.findAllByUserName(userName).get(0);
        for(Role role : user.getRoles()){
            for(Authority authority : role.getAuthorities()) {
                authoritieSet.add(authority);
            }
        }
        return authoritieSet;
    }

    /*
        角色部分的增删查改
     */
    @RequestMapping("/findallrole")
    @ResponseBody
    public List<Role> findAllRole() {
        return roleRepository.findAll();
    }
    @PostMapping("findallrole/{roleId}")
    @ResponseBody
    public List<Role> findByRoleId(@PathVariable("roleId") Integer roleId) {
        return roleRepository.findAllByRoleId(roleId);
    }

    @RequestMapping("/addrole")

    public String addRole(
            @RequestParam(value = "roleName") String roleName,
            @RequestParam(value = "authName") String authName) {
        Role role = new Role();
        Authority authority = authorityRepository.findAllByAuthorityName(authName).get(0);
        role.setRoleName(roleName);
        role.setAuthorities(new ArrayList<>());
        role.getAuthorities().add(authority);
        roleRepository.save(role);
        return "redirect:/addRoleToAuth";
    }
    @RequestMapping("/addAuthInfo")
    public String addAuthInfo(
            @RequestParam(value = "authName") String authName){
        Authority auth = new Authority();
        auth.setAuthorityName(authName);
        authorityRepository.save(auth);
        return "redirect:/addAuth";
    }
        /*
        给角色添加权限
     */
    @RequestMapping("/addroleauth")
    @ResponseBody
    public List<Role> addRoleAuth(
            @RequestParam(value = "roleName") String roleName,
            @RequestParam(value = "authName") String authName) {
        Role role = roleRepository.findAllByRoleName(roleName).get(0);
        Authority authority = authorityRepository.findAllByAuthorityName(authName).get(0);
        if (role.getAuthorities() == null) {
            role.setAuthorities(new ArrayList<>());
        }
        role.getAuthorities().add(authority);
        roleRepository.save(role);
        return roleRepository.findAll();
    }

    @GetMapping("/deleteByRoleId/{roleId}")
    public ModelAndView deleteByRoleId(@PathVariable("roleId") Integer roleId){
        entityService.deleteByRoleId(roleId);
        return  new ModelAndView("redirect:/quanxian");
    }


    /*
        权限部分的增删查改
     */
    @RequestMapping("/findallauth")
    @ResponseBody
    public List<Authority> findAllAuthority() {
        return authorityRepository.findAll();
    }
    @PostMapping("findallauth/{authorityId}")
    @ResponseBody
    public List<Authority> findByAuthId(@PathVariable("authorityId") Integer authorityId) {
        return authorityRepository.findAllByAuthorityId(authorityId);
    }

    @GetMapping("/deleteByAuthorityName/{authorityName}")
    public ModelAndView deleteByAuhorityName(@PathVariable("authorityName") String authorityName){
        entityService.deleteByAuthorityName(authorityName);
        return new ModelAndView("redirect:/quanxian");
    }

    /*
    在前端显示用户，角色和权限
*/
    @RequestMapping("/quanxian")
    public String Ask(Model model) {
        List<User> findUser = userRepository.findAll();
        List<Role> findRole = roleRepository.findAll();
        List<Authority> findAuth = authorityRepository.findAll();
        model.addAttribute("findUser", findUser);
        model.addAttribute("findRole", findRole);
        model.addAttribute("findAuth", findAuth);
        return "/quanxian";
    }
    /*
关联前端
*/
    @RequestMapping("/askByUserId")
    public String askByUserId(@PathParam("userId") Integer userId, Model model){
        if(userId == null){
        List<User> findUser = userRepository.findAll();
        model.addAttribute("findUser", findUser);
        }else{List<User> findUser =userRepository.findAllByUserId(userId);
            model.addAttribute("findUser",findUser);
        }
        return "/askId";
    }
    @RequestMapping("/askByUserName")
    public String askByUserName(@PathParam("userName") String userName, Model model){
        if(userName == null){
            List<User> findUserName = userRepository.findAll();
            model.addAttribute("findUserName", findUserName);
        }else{List<User> findUserName =userRepository.findAllByUserName(userName);
            model.addAttribute("findUserName",findUserName);
        }
        return "/askName";
    }
    @RequestMapping("/add")
    public String add(){
        return "/add";
    }
    @RequestMapping("/addUserToRole")
    public String addUser(Model model){
        List<User> findUser = userRepository.findAll();
        model.addAttribute("findUser", findUser);
        return "/addUserToRole";
    }
    @RequestMapping("/updateUserInfoFirst")
    public String updateUserInfo(@PathParam("userId") Integer userId,Model model){
        if(userId == null){
            List<User> findUser = userRepository.findAll();
            model.addAttribute("findUser", findUser);
        }else{List<User> findUser =userRepository.findAllByUserId(userId);
            model.addAttribute("findUser",findUser);
        }
        return "/updateUserInfoFirst";
    }
    @GetMapping("updateUserInfoThird/{userId2}")
    public ModelAndView updateUserInfoThird(@PathVariable("userId2") Integer userId, Model model) {
        model.addAttribute("userInfo",userRepository.findAllByUserId(userId));
        return new ModelAndView("/updateUserInfoThird","model",model);
    }
    @RequestMapping("/updateUserInfoThird")
    public String updateUserInfoThird(@RequestParam(value = "userId")Integer userId, @RequestParam(value = "userName")String userName, @RequestParam(value = "roleName")String roleName){
        User use = new User();
        Role role = roleRepository.findAllByRoleName(roleName).get(0);
        use.setUserId(userId);
        use.setUserName(userName);
        use.setRoles(new ArrayList<>());
        use.getRoles().add(role);
        userRepository.save(use);
        return "redirect:/updateUserInfoFirst";
    }
    @RequestMapping("/askByRoleId")
    public String askByRoleId(@PathParam("roleId") Integer roleId, Model model){
        if(roleId == null){
            List<Role> findRole = roleRepository.findAll();
            model.addAttribute("findRole", findRole);
        }else{List<Role> findRole =roleRepository.findAllByRoleId(roleId);
            model.addAttribute("findRole",findRole);
        }
        return "/askRoleId";
    }
    @RequestMapping("/addRoleToAuth")
    public String addRole(Model model){
        List<Role> findRole = roleRepository.findAll();
        model.addAttribute("findRole", findRole);
        return "/addRoleToAuth";
    }

    @RequestMapping("/updateRoleInfoFirst")
    public String updateRoleInfo(@PathParam("roleId") Integer roleId,Model model){
        if(roleId == null){
            List<Role> findRole = roleRepository.findAll();
            model.addAttribute("findRole", findRole);
        }else{List<Role> findRole =roleRepository.findAllByRoleId(roleId);
            model.addAttribute("findRole",findRole);
        }
        return "/updateRoleInfoFirst";
    }
    @GetMapping("updateRoleInfoThird/{roleId2}")
    public ModelAndView updateRoleInfoThird(@PathVariable("roleId2") Integer roleId, Model model) {
        model.addAttribute("roleInfo",roleRepository.findAllByRoleId(roleId));
        return new ModelAndView("/updateRoleInfoThird","model",model);
    }
    @RequestMapping("/updateRoleInfoThird")
    public String updateRoleInfoThird(@RequestParam("roleId")Integer roleId, @RequestParam("roleName")String roleName, @RequestParam("authName")String authName){
        Role role = new Role();
        Authority authority = authorityRepository.findAllByAuthorityName(authName).get(0);
        role.setRoleName(roleName);
        role.setRoleId(roleId);
        role.setAuthorities(new ArrayList<>());
        role.getAuthorities().add(authority);
        roleRepository.save(role);
        return "redirect:/updateRoleInfoFirst";
    }
    @RequestMapping("/askByAuthId")
    public String askByAuthId(@PathParam("authId") Integer authId, Model model){
        if(authId == null){
            List<Authority> findAuth = authorityRepository.findAll();
            model.addAttribute("findAuth", findAuth);
        }else{List<Authority> findAuth =authorityRepository.findAllByAuthorityId(authId);
            model.addAttribute("findAuth",findAuth);
        }
        return "/askAuthId";
    }
    @RequestMapping("/addAuth")
    public String addAuth(Model model){
        List<Authority> findAuth = authorityRepository.findAll();
        model.addAttribute("findAuth", findAuth);
        return "/addAuth";
    }
    @RequestMapping("/updateAuthInfoFirst")
    public String updateAuthInfo(@PathParam("authId") Integer authId,Model model){
        if(authId == null){
            List<Authority> findAuth = authorityRepository.findAll();
            model.addAttribute("findAuth", findAuth);
        }else{List<Authority> findAuth =authorityRepository.findAllByAuthorityId(authId);
            model.addAttribute("findAuth",findAuth);
        }
        return "/updateAuthInfoFirst";
    }
    @GetMapping("updateAuthInfoThird/{authorityId}")
    public ModelAndView updateAuthInfoThird(@PathVariable("authorityId") Integer authorityId, Model model) {
        model.addAttribute("authInfo",authorityRepository.findAllByAuthorityId(authorityId));
        return new ModelAndView("/updateAuthInfoThird","model",model);
    }
    @RequestMapping("/updateAuthInfoThird")
    public String updateAuthInfoThird(@RequestParam("authId")Integer authorityId, @RequestParam("authName")String authorityName){
        Authority auth = new Authority();
        auth.setAuthorityId(authorityId);
        auth.setAuthorityName(authorityName);
        authorityRepository.save(auth);
        return "redirect:/updateAuthInfoFirst";
    }



}


