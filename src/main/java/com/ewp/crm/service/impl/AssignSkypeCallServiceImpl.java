package com.ewp.crm.service.impl;

import com.ewp.crm.configs.VKConfigImpl;
import com.ewp.crm.models.AssignSkypeCall;
import com.ewp.crm.repository.interfaces.AssignSkypeCallRepository;
import com.ewp.crm.service.interfaces.AssignSkypeCallService;
import com.ewp.crm.service.interfaces.VKService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AssignSkypeCallServiceImpl extends CommonServiceImpl<AssignSkypeCall> implements AssignSkypeCallService {

	private final AssignSkypeCallRepository assignSkypeCallRepository;
	private final VKService vkService;

	@Autowired
	public AssignSkypeCallServiceImpl(AssignSkypeCallRepository assignSkypeCallRepository, @Lazy VKService vkService) {
		this.assignSkypeCallRepository = assignSkypeCallRepository;
		this.vkService = vkService;
	}

    @Override
    public void update(AssignSkypeCall assignSkypeCall) {
        super.update(assignSkypeCall);
        vkService.sendFirstSkypeNotification(assignSkypeCall.getToAssignSkypeCall(),
                assignSkypeCall.getSkypeCallDate(),
                VKConfigImpl.firstSkypeNotificationType.UPDATE);
    }

    @Override
	public void addSkypeCall(AssignSkypeCall assignSkypeCall) {
		assignSkypeCallRepository.saveAndFlush(assignSkypeCall);
		vkService.sendFirstSkypeNotification(assignSkypeCall.getToAssignSkypeCall(),
                assignSkypeCall.getSkypeCallDate(),
                VKConfigImpl.firstSkypeNotificationType.CREATE);
	}

	@Override
	public List<AssignSkypeCall> getAssignSkypeCallIfNotificationWasNoSent() {
		return assignSkypeCallRepository.getAssignSkypeCallIfNotificationWasNoSent();
	}

	@Override
	public List<AssignSkypeCall> getAssignSkypeCallIfCallDateHasAlreadyPassedButHasNotBeenClearedToTheClient() {
		return assignSkypeCallRepository.getAssignSkypeCallIfCallDateHasAlreadyPassedButHasNotBeenClearedToTheClient();
	}

	@Override
	public void deleteByIdSkypeCall(Long id) {
        AssignSkypeCall assignSkypeCall = get(id);
        if (assignSkypeCall != null) {
            vkService.sendFirstSkypeNotification(assignSkypeCall.getToAssignSkypeCall(),
                    assignSkypeCall.getSkypeCallDate(),
                    VKConfigImpl.firstSkypeNotificationType.DELETE);
            assignSkypeCallRepository.delete(assignSkypeCall);
        }
    }

	@Override
	public Optional<AssignSkypeCall> getAssignSkypeCallByClientId(Long clientId) {
		return Optional.ofNullable(assignSkypeCallRepository.getAssignSkypeCallByClientId(clientId));
	}
}