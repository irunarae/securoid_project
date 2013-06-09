    protected static int r_Generate() throws NoSuchAlgorithmException{
    	return Integer.parseInt(KeyGenerator.getInstance("HmacMD5").generateKey().toString().substring(37, 40),16);
	}
	
	protected static byte[] otp_Key_Generate() throws NoSuchAlgorithmException{
		byte[] otp_key = new byte[16];
		KeyGenerator.getInstance("HmacMD5").generateKey().toString().getBytes(32, 40, otp_key, 0);
		KeyGenerator.getInstance("HmacMD5").generateKey().toString().getBytes(32, 40, otp_key, 8);
		return otp_key;
	}

