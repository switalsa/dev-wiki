package pl.switalski.wiki.java.hibernate.beans;

import java.util.List;

import javax.sql.DataSource;

import org.hibernate.FlushMode;
import org.hibernate.Transaction;
import org.hibernate.classic.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import pl.switalski.wiki.java.hibernate.model.PhoneNumber;
import pl.switalski.wiki.java.hibernate.model.TelecommunicationObject;

@Component
@Transactional(propagation = Propagation.MANDATORY)
public class PersistenceService {
	
	@Autowired
	protected HibernateTemplate hibernateTemplate;
	
	@Autowired
	private DataSource dataSource;
	
	/**
	 * Saves specified entity.
	 * 
	 * @param entity
	 *            Entity
	 */
	public void save(Object entity) {
		hibernateTemplate.save(entity);
	}

	public TelecommunicationObject getTelecommunicationObject(int id) {
		TelecommunicationObject object = (TelecommunicationObject) hibernateTemplate.get(TelecommunicationObject.class, id);
		return object;
	}

	public PhoneNumber getNumber(int id) {
		PhoneNumber number = (PhoneNumber) hibernateTemplate.get(PhoneNumber.class, id);
		return number;
	}
	
	public List<?> getResult(String query) {
		hibernateTemplate.getSessionFactory().getCurrentSession().setFlushMode(FlushMode.AUTO);
		List<?> objects = hibernateTemplate.find(query);
		return objects;
	}
	
	public HibernateTemplate getHibernateTemplate() {
		return hibernateTemplate;
	}

	public Session getSession() {
		return hibernateTemplate.getSessionFactory().getCurrentSession();
	}
	
	public Transaction getTransaction() {
		return hibernateTemplate.getSessionFactory().getCurrentSession().getTransaction();
	}

}
