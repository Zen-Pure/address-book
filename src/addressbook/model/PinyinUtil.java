package addressbook.model;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

//拼音转换工具类:基于 pinyin4j 库实现
public class PinyinUtil {
	
	private static final HanyuPinyinOutputFormat FORMAT;
	
	static {
		FORMAT=new HanyuPinyinOutputFormat();
		FORMAT.setCaseType(HanyuPinyinCaseType.LOWERCASE);      //小写
		FORMAT.setToneType(HanyuPinyinToneType.WITHOUT_TONE);   //无声调
		FORMAT.setVCharType(HanyuPinyinVCharType.WITH_V);       //ü -> v
	}
	
	//获取小写全拼

	public static String getFullPinyin(String chinese) {
		if(chinese==null||chinese.isEmpty()) return "";
		
		StringBuilder sb=new StringBuilder();
		for(char c:chinese.toCharArray()) {
			if(Character.toString(c).matches("[\\u4E00-\\u9FA5]")) {
				//如果是汉字，转拼音
				try {
					String[] pinyinArray=PinyinHelper.toHanyuPinyinStringArray(c, FORMAT);
					if(pinyinArray!=null&&pinyinArray.length>0) {
						sb.append(pinyinArray[0]); //遇到多音字，取第一个读音
					}
				} catch(BadHanyuPinyinOutputFormatCombination e) {
					sb.append(c);
				}
			} else {
				//非汉字原样保留
				sb.append(c);
		}
	}
		return sb.toString().toLowerCase();
	}
	
	//获取大写首字母
	public static String getFirstLetter(String chinese) {
		if(chinese==null||chinese.isEmpty()) return "#";
		
		char firstChar=chinese.charAt(0);
		
		//汉字
		if(Character.toString(firstChar).matches("[\\u4E00-\\u9FA5]")) {
			try {
				String[] pinyinArray=PinyinHelper.toHanyuPinyinStringArray(firstChar, FORMAT);
				if(pinyinArray!=null&&pinyinArray.length>0) {
					return String.valueOf(pinyinArray[0].charAt(0)).toUpperCase();
				}
			} catch(BadHanyuPinyinOutputFormatCombination e) {
				return "#";
			}
		}

		//英文直接转大写
		if(Character.isLetter(firstChar)) {
			return String.valueOf(Character.toUpperCase(firstChar));
		}
		
		//数字或其他符号
		return "#";
	}

	//获取每个字的大写拼音首字母
	public static String getInitials(String chinese) {
		if(chinese==null||chinese.isEmpty()) return "";
		
		StringBuilder sb=new StringBuilder();
		for(char c:chinese.toCharArray()) {
			sb.append(getFirstLetter(String.valueOf(c)));
		}
		return sb.toString();
	}
}
