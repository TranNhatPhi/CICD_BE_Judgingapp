package com.project.judging.Mapper.Impl;

import com.project.judging.DTOs.AdminDTO.JudgeAssignedDTO;
import com.project.judging.DTOs.AdminDTO.ProjectMarked.JudgeDTOwithMarked;
import com.project.judging.DTOs.JudgeDTO;
import com.project.judging.Mapper.Mapper;
import com.project.judging.Entities.Judge;
import com.project.judging.Repositories.MarkingRoundRepository;
import org.springframework.stereotype.Component;

@Component
public class JudgeMapper implements Mapper<Judge, JudgeDTO> {

    private final SemesterMapper semesterMapper;
    private final MarkingRoundRepository markingRoundRepository;

    public JudgeMapper(SemesterMapper semesterMapper, MarkingRoundRepository markingRoundRepository) {
        this.semesterMapper = semesterMapper;
        this.markingRoundRepository = markingRoundRepository;
    }

    @Override
    public JudgeDTO toDto(Judge judge) {
        JudgeDTO judgeDTO = new JudgeDTO();
        judgeDTO.setId(judge.getId());
        judgeDTO.setAccount(judge.getAccount());
        judgeDTO.setPwd(judge.getPlainPwd());
        judgeDTO.setFirstName(judge.getFirstName());
        judgeDTO.setLastName(judge.getLastName());
        judgeDTO.setEmail(judge.getEmail());
        judgeDTO.setPhone(judge.getPhone());
        judgeDTO.setDescription(judge.getDescription());
        judgeDTO.setNumberOfProject(judge.getNumberOfProject());
        judgeDTO.setSemester(judge.getSemester() != null
                                    ? semesterMapper.toDto(judge.getSemester())
                                    : null);

        return judgeDTO;
    }

    @Override
    public Judge toEntity(JudgeDTO judgeDTO) {
        Judge judge = new Judge();
        judge.setAccount(judgeDTO.getAccount());
        judge.setPwd(judgeDTO.getPwd());
        judge.setFirstName(judgeDTO.getFirstName());
        judge.setLastName(judgeDTO.getLastName());
        judge.setEmail(judgeDTO.getEmail());
        judge.setPhone(judgeDTO.getPhone());
        judge.setDescription(judgeDTO.getDescription());
        return judge;
    }

    // Update new method for Judge with marked (round 1)
    public JudgeDTOwithMarked toDtoWithMarked(Judge judge, Integer projectId) {
        JudgeDTOwithMarked judgeDTOwithMarked = new JudgeDTOwithMarked();
        judgeDTOwithMarked.setId(judge.getId());
        judgeDTOwithMarked.setAccount(judge.getAccount());
        judgeDTOwithMarked.setPwd(judge.getPlainPwd());
        judgeDTOwithMarked.setFirstName(judge.getFirstName());
        judgeDTOwithMarked.setLastName(judge.getLastName());
        judgeDTOwithMarked.setEmail(judge.getEmail());
        judgeDTOwithMarked.setPhone(judge.getPhone());
        judgeDTOwithMarked.setDescription(judge.getDescription());
        Integer mark = this.markingRoundRepository.getTotalMarkedByJudge(judge.getId(),projectId);
        judgeDTOwithMarked.setMark(mark);
        return judgeDTOwithMarked;
    }
}