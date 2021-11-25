package com.kostapo.bot;

import com.kostapo.bot.repository.QiwiRepository;
import com.kostapo.bot.repository.UserRepository;
import com.kostapo.bot.repository.WithdrawRepository;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.*;
import java.util.List;

@RequestMapping
@Controller
public class HelloController {

	private final UserRepository userRepository;
	private final QiwiRepository qiwiRepository;
	private final WithdrawRepository withdrawRepository;
	private final Bot myBot;


	public HelloController(UserRepository userRepository, QiwiRepository qiwiRepository, WithdrawRepository withdrawRepository, Bot myBot) {
		this.userRepository = userRepository;
		this.qiwiRepository = qiwiRepository;
		this.withdrawRepository = withdrawRepository;
		this.myBot = myBot;
	}



	// default Decorator
	@RequestMapping("/login")
	public String usermenu() {

		return "login";
	}

	@GetMapping("/")
	public String index() {

		return "login";
	}


	@GetMapping("/index")
	public String admin(Model model) {
		model.addAttribute("users",userRepository.findAll());
		model.addAttribute("balance", qiwiRepository.sumBalance());
		model.addAttribute("noBalance", qiwiRepository.sumNoBalance());
		return "index";
	}

	@GetMapping("/info")
	public String info(Model model) {
		model.addAttribute("users",userRepository.findAll());
		model.addAttribute("send", new SendAll());
		return "info";
	}

	@GetMapping("/users")
	public String user(Model model) {
		model.addAttribute("users",userRepository.findAll());
		return "users";
	}

	@GetMapping("/pay")
	public String pay(Model model) {
		model.addAttribute("withdraw",withdrawRepository.findAllByStatus("WAIT"));
		model.addAttribute("withdrawSuc",withdrawRepository.findAllByStatus("SUCCESS"));
		return "pay";
	}

	@GetMapping("/payment")
	public String payment(Model model) {
		model.addAttribute("qiwiPay",qiwiRepository.findAll());
		return "payment";
	}

	@GetMapping("/details/{id}")
	public String details(Model model, @PathVariable Integer id) {
		model.addAttribute("qiwi",qiwiRepository.userQiwiPay(String.valueOf(id)));
		model.addAttribute("id", userRepository.getId(id));
		model.addAttribute(	"balance", userRepository.getBalance(id));
		model.addAttribute(	"balanceVkl", userRepository.getBalanceVkl(id));
		model.addAttribute("chatId", userRepository.getchatId(id));
		model.addAttribute("lvl", userRepository.getlevel(id));
		model.addAttribute("username", userRepository.getUsername(id));
		model.addAttribute("pay", userRepository.getPay(id));
		model.addAttribute("purse", userRepository.getPurse(id));
		model.addAttribute("ban", userRepository.getBan(id));
		model.addAttribute("top", userRepository.getTop(id));
		model.addAttribute("unt", userRepository.getUnt(id));
		model.addAttribute("block", userRepository.getBlock(id));
		model.addAttribute("password", userRepository.getPassword(id));
		model.addAttribute("lastMessage", userRepository.getLastMessage(id));
		model.addAttribute("update", new updateUser());
		return "details";
	}

	@PostMapping("/pay/success/{id_draw}")
	public String withdrawSuccess(@PathVariable("id_draw") Integer id_draw) {
		withdrawRepository.updateStatus(id_draw);
		return "redirect:/pay";
	}

	@PostMapping("/details/delete/{idPay}")
	public String qiwiDeletePay(@PathVariable String idPay) {
		String id = qiwiRepository.findUserByBillId(idPay);
		qiwiRepository.deleteQiwiIdPay(idPay);
		System.out.println(idPay);
		return "redirect:/details/" + id;
	}

	@PostMapping("/send/message")
	public String sendMessage(@ModelAttribute SendAll sendAll ,Model model) throws TelegramApiException {
		model.addAttribute("send", sendAll);
		System.out.println(sendAll.getText());

		List<Integer> listId = userRepository.findId();

		System.out.println("Количество пользователей:" + listId.size());

			try {
				for(Integer person : listId){
					myBot.execute(new SendMessage(String.valueOf(person),sendAll.getText()).setParseMode(ParseMode.MARKDOWN));
					Thread.sleep(50);
					System.out.println("Стоп отправка");
				}
			} catch (NullPointerException | InterruptedException e){
				e.printStackTrace();
			}



		return "redirect:/info";
	}

	@PostMapping("/send/photo")
	public String sendPhoto(@ModelAttribute SendAll sendAll ,Model model) throws TelegramApiException {
		model.addAttribute("send", sendAll);
		System.out.println(sendAll.getText());

		List<Integer> listId = userRepository.findId();

		System.out.println("Количество пользователей:" + listId.size());
		try {
			for(Integer person : listId){
				myBot.execute(new SendPhoto().setChatId(String.valueOf(person)).setPhoto(sendAll.getUrl()).setCaption(sendAll.getText()).setParseMode(ParseMode.MARKDOWN));
				Thread.sleep(1000);
			}
		} catch (NullPointerException | InterruptedException e){
			e.printStackTrace();
		}


		return "redirect:/info";
	}


	@PostMapping("/details/update/{id}")
	public String updateUser(@PathVariable("id") Integer id, @ModelAttribute updateUser updateUser, Model model) {
		model.addAttribute("update", updateUser);
		if(updateUser.getBalance() != null){
			userRepository.updateBalance(updateUser.getBalance(), id);
		} else {
			userRepository.updateBalance((userRepository.getBalance(id)), id);
		}
		if(updateUser.getTop() != null){
			userRepository.updateTop(updateUser.getTop(), id);
		} else {
			userRepository.updateTop((userRepository.getTop(id)), id);
		}
		if(updateUser.getUnt() != null){
			userRepository.updateUnt(updateUser.getUnt(), id);
		} else {
			userRepository.updateUnt((userRepository.getUnt(id)), id);
		}
		if(updateUser.getBalanceVkl() != null){
			userRepository.updateBalanceVkl(updateUser.getBalanceVkl(), id);
		} else {
			userRepository.updateBalanceVkl((userRepository.getBalanceVkl(id)), id);
		}
		if (updateUser.getLvl() != null){
			userRepository.updateLvL(updateUser.getLvl(),id);
		} else {
			userRepository.updateLvL((userRepository.getlevel(id)), id);
		}
		if (!updateUser.getPurse().isEmpty()){
			userRepository.updatePurse(updateUser.getPurse(),id);
		} else {
			userRepository.updatePurse((userRepository.getPurse(id)), id);
		}
		if (!updateUser.getLastMessage().isEmpty()){
			userRepository.updateLastMessage(updateUser.getLastMessage(),id);
		} else {
			userRepository.updateLastMessage((userRepository.getLastMessage(id)), id);
		}
		if (!updateUser.getUsername().isEmpty()){
			userRepository.updateUsername(updateUser.getUsername(),id);
		} else {
			userRepository.updateUsername((userRepository.getUsername(id)), id);
		}
		if (!updateUser.getPassword().isEmpty()){
			userRepository.updatePass(updateUser.getPassword(),id);
		} else {
			userRepository.updatePass((userRepository.getPassword(id)), id);
		}
		if (updateUser.getPay() != null){
			userRepository.updatePayment_Num(updateUser.getPay(),id);
		} else {
			userRepository.updatePayment_Num((userRepository.getPay(id)), id);
		}

		userRepository.ban(updateUser.getBan(),id);
		userRepository.block(updateUser.getBlock(),id);
		return "redirect:/details/" + id;
	}

	@PostMapping("/user/ban/{chat_id}")
	public String userBan(@PathVariable("chat_id") Integer chat_id) {
		userRepository.ban(true,chat_id);
		return "redirect:/users";
	}

	@PostMapping("/user/unban/{chat_id}")
	public String userUnban(@PathVariable("chat_id") Integer chat_id) {
		userRepository.unban(false, chat_id);
		return "redirect:/users";
	}


	@GetMapping("/assets/js/vendors/jquery-3.2.1.min.js")
	public String adminon() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (!(auth instanceof AnonymousAuthenticationToken))
			return "redirect:/index";

		// if it is not authenticated, then go to the index...
		// other things ...
		return "redirect:/login";
	}

	@GetMapping("/resources/templates/css/{code}.css")
	@ResponseBody
	public ResponseEntity<String> stylesOne(@PathVariable("code") String code) throws IOException {
		InputStream is = getClass().getClassLoader().getResourceAsStream("resources/templates/css/"+code+".css");
		BufferedReader bf = new BufferedReader(new InputStreamReader(is));
		StringBuffer sb = new StringBuffer();
		String line = null;
		while((line = bf.readLine()) != null){
			sb.append(line+"\n");
		}

		final HttpHeaders httpHeaders= new HttpHeaders();
		httpHeaders.add("Content-Type", "text/css; charset=utf-8");
		return new ResponseEntity<String>( sb.toString(), httpHeaders, HttpStatus.OK);
	}

	@GetMapping("/resources/static/css/{code}.css")
	@ResponseBody
	public ResponseEntity<String> styles(@PathVariable("code") String code) throws IOException {
		InputStream is = getClass().getClassLoader().getResourceAsStream("static/css/"+code+".css");
		BufferedReader bf = new BufferedReader(new InputStreamReader(is));
		StringBuffer sb = new StringBuffer();
		String line = null;
		while((line = bf.readLine()) != null){
			sb.append(line+"\n");
		}

		final HttpHeaders httpHeaders= new HttpHeaders();
		httpHeaders.add("Content-Type", "text/css; charset=utf-8");
		return new ResponseEntity<String>( sb.toString(), httpHeaders, HttpStatus.OK);
	}

	@GetMapping("/static/css/dist/css/{code}.css")
	@ResponseBody
	public ResponseEntity<String> stylesTwo(@PathVariable("code") String code) throws IOException {
		InputStream is = getClass().getClassLoader().getResourceAsStream("resources/static/dist/css/"+code+".css");
		BufferedReader bf = new BufferedReader(new InputStreamReader(is));
		StringBuffer sb = new StringBuffer();
		String line = null;
		while((line = bf.readLine()) != null){
			sb.append(line+"\n");
		}

		final HttpHeaders httpHeaders= new HttpHeaders();
		httpHeaders.add("Content-Type", "text/css; charset=utf-8");
		return new ResponseEntity<String>( sb.toString(), httpHeaders, HttpStatus.OK);
	}








}
