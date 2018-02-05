package java100.app.web.json;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import java100.app.domain.Account;
import java100.app.domain.Item;
import java100.app.domain.Photo;
import java100.app.service.ItemService;
import java100.app.service.UserService;
import net.coobird.thumbnailator.Thumbnails;

@RestController
@RequestMapping("/item")
public class ItemController {

    int it_no;
    
    @Autowired ServletContext servletContext;
    @Autowired ItemService itemService;
    @Autowired UserService userService;
    @RequestMapping("add")
    public int add(Item item, HttpSession session) throws Exception {
        Account account = (Account) session.getAttribute("loginUser");
        item.setUserNo(account.getAccountsNo());
        itemService.add(item);
        it_no = item.getItemNo();
        return it_no;
}
    @RequestMapping("upload")
    public String upload(MultipartHttpServletRequest photo) throws Exception {
        String uploadDir = servletContext.getRealPath("/download");
        ArrayList<Photo> uploadFiles = new ArrayList<>();
        Iterator<String> files = photo.getFileNames();
        while(files.hasNext()){
            String uploadFile = files.next();

            MultipartFile part = photo.getFile(uploadFile);

            String filename = this.writeUploadFile(part, uploadDir);
            //썸네일 생성 메소드 호출 = 이름리턴     ** Thumbnail(저장경로,원본파일이름,썸네일너비,썸네일높이)

            String Thumbnail = this.Thumbnail(uploadDir,filename,50,50);

            uploadFiles.add(new Photo(filename,Thumbnail));
        }
        itemService.upload(it_no, uploadFiles);
        it_no = 0;
        return "success";
    }
    
    
    
//    @RequestMapping("list")
//    public String rentlist(
//            @RequestParam(value="pn", defaultValue="1") int pageNo,
//            @RequestParam(value="ps", defaultValue="6") int pageSize,
//            @RequestParam(value="words", required=false) String[] words,
//            @RequestParam(value="oc", required=false) String orderColumn,
//            @RequestParam(value="al", required=false) String align,
//            Model model) throws Exception {
//        
//        if (pageNo < 1) {
//            pageNo = 1;
//        }
//        if (pageSize < 6 || pageSize > 15) {
//            pageSize = 6;
//        } 
//        
//        HashMap<String,Object> options = new HashMap<>();
//        if (words != null && words[0].length() > 0) {
//            options.put("words", words);
//        }
//        options.put("orderColumn", orderColumn);
//        options.put("align", align);
//        
//        int totalCount = itemService.getTotalCount();
//        int lastPageNo = totalCount / pageSize;
//        if ((totalCount % pageSize) > 0) {
//            lastPageNo++;
//        }
//        
//        // view 컴포넌트가 사용할 값을 Model에 담는다.
//        model.addAttribute("pageNo", pageNo);
//        model.addAttribute("lastPageNo", lastPageNo);
//        model.addAttribute("list", itemService.list(pageNo, pageSize, options));
//        
//        
//        return "item/list";
//    }
//    @RequestMapping("{no}")
//    public String view(@PathVariable int no, Model model) throws Exception {
//
//
//        model.addAttribute("item", itemService.getItem(no));
//        model.addAttribute("user", userService.getUser(no));
//
//        return "item/view";
//    }
    
    
    
    
    
    long prevMillis = 0;
    int count = 0;
    
    synchronized private String getNewFilename(String filename) {
        long currMillis = System.currentTimeMillis();
        if (prevMillis != currMillis) {
            count = 0;
            prevMillis = currMillis;
        }
        return currMillis + "_" + count++ + extractFileExtName(filename); 
    }

    private String extractFileExtName(String filename) {
        int dotPosition = filename.lastIndexOf(".");
        if (dotPosition == -1)
            return "";
        return filename.substring(dotPosition);
    }

    private String writeUploadFile(MultipartFile part, String path) throws IOException{

        String filename = getNewFilename(part.getOriginalFilename());
        part.transferTo(new File(path + "/" + filename));

        return filename;
    }

  //썸네일 저장 코드(s_원본파일로 저장)
    private String Thumbnail(String uploadDir, String filename, int width, int height) {
        File image = new File(uploadDir+"//"+filename); 
        File thumbnail = new File(uploadDir+"//s_"+filename); 
        if (image.exists()) { 
            try {
                int pos = filename.lastIndexOf(".");
                String format = filename.substring(pos + 1);
                Thumbnails.of(image).size(width, height).outputFormat(format).toFile(thumbnail);
            } catch (IOException e) {
                e.printStackTrace();
            } 
        }
        return "s_"+filename;
    }
}








