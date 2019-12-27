package winter.findGuider.web.api;

import entities.Contract;
import entities.Guider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import services.Mail.MailService;
import services.account.AccountRepository;
import services.guider.GuiderService;

import java.io.File;
import java.io.FileInputStream;
import java.util.List;

@RestController
@RequestMapping(path = "/Guider", produces = "application/json")
@CrossOrigin(origins = "*")
public class GuiderController {
    private Logger logger = LoggerFactory.getLogger(getClass());
    private GuiderService guiderService;
    private MailService mailService;
    private AccountRepository accountRepository;

    @Autowired
    public GuiderController(GuiderService gs, MailService ms, AccountRepository ar) {
        this.guiderService = gs;
        this.mailService = ms;
        this.accountRepository = ar;
    }

    @RequestMapping("/CreateContract")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Long> createContract(@RequestBody Contract newGuiderContract) {
        try {
            long contractId = guiderService.createGuiderContract(newGuiderContract);
            return new ResponseEntity<>(contractId, HttpStatus.OK);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping("/Edit")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Guider> editGuider(@RequestBody Guider guiderNeedUpdate) {
        try {
            guiderService.updateGuiderWithId(guiderNeedUpdate);
            return new ResponseEntity<>(guiderService.findGuiderWithID(guiderNeedUpdate.getGuider_id()), HttpStatus.OK);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping("/Activate/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Guider> activateGuider(@PathVariable("id") long id) {
        try {
            long activatedId = guiderService.activateGuider(id);
            return new ResponseEntity<>(guiderService.findGuiderWithID(activatedId), HttpStatus.OK);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }

    }

    @RequestMapping("/Deactivate/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Guider> deactivateGuider(@PathVariable("id") long id) {
        try {
            long deactivatedId = guiderService.deactivateGuider(id);
            return new ResponseEntity<>(guiderService.findGuiderWithID(deactivatedId), HttpStatus.OK);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/Search/{key}/{page}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<Guider>> searchGuider(@PathVariable("key") String key, @PathVariable("page") int page) {
        try {
            return new ResponseEntity<>(guiderService.searchGuiderByName(key, page), HttpStatus.OK);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.NO_CONTENT);
        }
    }

    @RequestMapping("/SearchPageCount/{key}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Integer> searchGuiderPageCount(@PathVariable("key") String key) {
        try {
            return new ResponseEntity<>(guiderService.searchGuiderByNamePageCount(key), HttpStatus.OK);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.NO_CONTENT);
        }
    }

    @RequestMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Guider> findGuider(@PathVariable("id") long id) {
        try {
            return new ResponseEntity<>(guiderService.findGuiderWithID(id), HttpStatus.OK);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping("/guiderByPostId")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Guider> findGuiderByPostId(@RequestParam("post_id") long post_id) {
        try {
            return new ResponseEntity<>(guiderService.findGuiderWithPostId(post_id), HttpStatus.OK);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping("/getTopGuiderByRate")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<Guider>> getTop5guiderByRate() {
        try {
            return new ResponseEntity<>(guiderService.getTopGuiderByRate(), HttpStatus.OK);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping("/getTopGuiderByContribute")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<Guider>> getTop5guiderByContribute() {
        try {
            return new ResponseEntity<>(guiderService.getTopGuiderByContribute(), HttpStatus.OK);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping("/getAllContract")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<Contract>> getAllContract() {
        try {
            return new ResponseEntity<>(guiderService.getAllContract(), HttpStatus.OK);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping("/AcceptContract")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Boolean> acceptContract(@RequestParam("guider_id") long guider_id, @RequestParam("contract_id") long contract_id) {
        try {
            guiderService.linkGuiderWithContract(guider_id, contract_id);
            boolean isMailVerified = accountRepository.isMailVerified(guider_id);
            if (isMailVerified) {
                String email = accountRepository.getEmail((int) guider_id);
                String content = mailService.acceptContractMailContent(guider_id);
                mailService.sendMail(email, "Your TravelWLocal Contract Was Accepted", content);
            }
            return new ResponseEntity<>(true, HttpStatus.OK);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return new ResponseEntity<>(false, HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping("/RejectContract")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Boolean> rejectContract(@RequestParam("guider_id") long guider_id, @RequestParam("contract_id") long contract_id) {
        try {
            guiderService.rejectContract(contract_id);
            boolean isMailVerified = accountRepository.isMailVerified(guider_id);
            if (isMailVerified) {
                String email = accountRepository.getEmail((int) guider_id);
                String content = mailService.rejectContractMailContent(guider_id);
                mailService.sendMail(email, "Your TravelWLocal Contract Was Refuse", content);
            }
            return new ResponseEntity<>(true, HttpStatus.OK);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return new ResponseEntity<>(false, HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping("/UploadDocument")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<String> UploadDocument(@RequestParam("file") MultipartFile file, @RequestParam("contract_id") long contract_id) {
        try {
            if (file.isEmpty()) {
                return new ResponseEntity<>("Please select a file to upload", HttpStatus.OK);
            }
            if (!file.getOriginalFilename().contains(".pdf")) {
                return new ResponseEntity<>("We only accept pdf file", HttpStatus.OK);
            }
            guiderService.uploadContractDocument(file, contract_id);
            return new ResponseEntity<>("Upload Success", HttpStatus.OK);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping(value = "/DownloadDocument", produces = "application/pdf")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<InputStreamResource> downloadDocument(@RequestParam("contract_id") long contract_id) {
        try {
            File pdfFile = guiderService.getDocumentToDownload(contract_id);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType("application/pdf"));
            headers.add("Access-Control-Allow-Origin", "*");
            headers.add("Access-Control-Allow-Methods", "GET, POST, PUT");
            headers.add("Access-Control-Allow-Headers", "Content-Type");
            headers.add("Content-Disposition", "filename=" + pdfFile.getName());
            headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
            headers.add("Pragma", "no-cache");
            headers.add("Expires", "0");
            headers.setContentLength(pdfFile.length());
            return new ResponseEntity<>(new InputStreamResource(new FileInputStream(pdfFile)), headers, HttpStatus.OK);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping("/getGuider/{guider_id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Guider> getGuider(@PathVariable("guider_id") long guider_id) {
        try {


            return new ResponseEntity<>(guiderService.findGuiderWithID(guider_id), HttpStatus.OK);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }
  
}
