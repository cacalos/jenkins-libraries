allow_scm_jenkinsfile = true
libraries{
	common {
		merge = true
	}
    email{
        fail_root_mail_lists = "cacalos@naver.com"
        success_root_mail_lists = "cacalos@korea.com"
		merge = true
    }
	merge = true
}

application_environments{
    dev{
        ip_addresses = [ "0.0.0.3", "0.0.0.7" ]
    }
    prod{
        long_name = "Production"
        ip_addresses = [ "0.0.1.1", "0.0.1.2", "0.0.1.3", "0.0.1.4" ]
    }
}

